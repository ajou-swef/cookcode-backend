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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
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
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AccessDeniedHandler accessDeniedHandler,
            AuthenticationEntryPoint authenticationEntryPoint
    ) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.GET, "/api/v1/account/password").permitAll()
                .requestMatchers("/api/v1/account/signin", "/api/v1/account/signup", "/api/v1/account/check", "/health", "/api/v1/account/email").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlerFilter, JwtAuthenticationFilter.class);

        return http.build();
    }
}
