package com.swef.cookcode.common.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.swef.cookcode.common.ErrorCode;
import com.swef.cookcode.common.ErrorResponse;
import com.swef.cookcode.common.filter.ExceptionHandlerFilter;
import com.swef.cookcode.common.jwt.JwtAuthenticationFilter;
import java.io.PrintWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final ExceptionHandlerFilter exceptionHandlerFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(ObjectMapper objectMapper) {
        return (request, response, e) -> {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            String json = objectMapper.writeValueAsString(ErrorResponse.of(ErrorCode.ACCESS_DENIED));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            PrintWriter writer = response.getWriter();
            writer.write(json);
            writer.flush();
        };
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper) {
        return (request, response, e) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            String json = objectMapper.writeValueAsString(ErrorResponse.of(ErrorCode.UNAUTHENTICATED_USER));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            PrintWriter writer = response.getWriter();
            writer.write(json);
            writer.flush();
        };
    }

    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_INFLUENCER > ROLE_USER");
        return roleHierarchy;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AccessDeniedHandler accessDeniedHandler,
            AuthenticationEntryPoint authenticationEntryPoint
    ) throws Exception {
        AuthorityAuthorizationManager<RequestAuthorizationContext> auth = AuthorityAuthorizationManager.hasRole("USER");
        auth.setRoleHierarchy(roleHierarchy());
        return http.authorizeHttpRequests(requests -> {
                    requests
                    .requestMatchers(HttpMethod.GET, "/api/v1/account/password").permitAll()
                    .requestMatchers("/api/v1/account/signin", "/api/v1/account/signup", "/api/v1/account/check", "/health", "/api/v1/account/email").permitAll()
                    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                    .anyRequest().access(auth);
                }).cors(it -> {})
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(it -> {
                    it.accessDeniedHandler(accessDeniedHandler);
                    it.authenticationEntryPoint(authenticationEntryPoint);
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlerFilter, JwtAuthenticationFilter.class)
                .build();
    }
}
