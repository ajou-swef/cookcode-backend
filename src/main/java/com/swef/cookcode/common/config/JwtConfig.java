package com.swef.cookcode.common.config;

import com.swef.cookcode.common.jwt.Jwt;
import com.swef.cookcode.common.jwt.JwtAuthenticationFilter;
import com.swef.cookcode.common.jwt.JwtAuthenticationProvider;
import com.swef.cookcode.common.jwt.JwtUtil;
import com.swef.cookcode.common.util.Util;
import com.swef.cookcode.user.service.UserService;
import com.swef.cookcode.user.service.UserSimpleService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.jwt")
@Getter
@Setter
public class JwtConfig {
  private String issuer;
  private String clientSecret;
  private Token accessToken;
  private Token refreshToken;
  private String blackListPrefix;
  @Getter
  @Setter
  public static class Token {
    private String header;
    private int expirySeconds;

    @Override
    public String toString() {
      return "header: "+header+" expirySeconds: "+expirySeconds;
    }
  }

  @Bean
  @Qualifier("accessJwt")
  public Jwt accessJwt() {
    return new Jwt(
        this.issuer,
        this.clientSecret,
        this.accessToken.expirySeconds);
  }

  @Bean
  @Qualifier("refreshJwt")
  public Jwt refreshJwt() {
    return new Jwt(
        this.issuer,
        this.clientSecret,
        this.refreshToken.expirySeconds);
  }

  @Bean
  public JwtAuthenticationProvider jwtAuthenticationProvider(JwtUtil jwtUtil,
                                                             UserService userService) {
    return new JwtAuthenticationProvider(jwtUtil, userService);
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil, Util util,
                                                         UserSimpleService userSimpleService) {
    return new JwtAuthenticationFilter(jwtUtil, util, userSimpleService);
  }
}
