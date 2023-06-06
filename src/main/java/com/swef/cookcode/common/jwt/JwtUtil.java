package com.swef.cookcode.common.jwt;

import static com.swef.cookcode.common.ErrorCode.BLACKLIST_TOKEN_REQUEST;
import static com.swef.cookcode.common.ErrorCode.INVALID_REFRESH_TOKEN_REQUEST;
import static com.swef.cookcode.common.ErrorCode.REDIS_TOKEN_NOT_FOUND;
import static com.swef.cookcode.common.ErrorCode.TOKEN_EXPIRED;
import static com.swef.cookcode.common.ErrorCode.TOKEN_NOT_EXPIRED;
import static com.swef.cookcode.common.ErrorCode.TOKEN_USER_ID_NOT_MATCHED;
import static java.util.Objects.isNull;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.util.StringUtils.hasText;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swef.cookcode.common.ApiResponse;
import com.swef.cookcode.common.ErrorResponse;
import com.swef.cookcode.common.config.JwtConfig;
import com.swef.cookcode.common.error.exception.AuthErrorException;
import com.swef.cookcode.common.error.exception.InvalidRequestException;
import com.swef.cookcode.common.jwt.Jwt;
import com.swef.cookcode.common.jwt.claims.AccessClaim;
import com.swef.cookcode.common.jwt.claims.RefreshClaim;
import com.swef.cookcode.common.service.RedisService;
import com.swef.cookcode.user.domain.User;
import com.swef.cookcode.user.dto.response.SignInResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtUtil {

  private final Jwt accessJwt;

  private final Jwt refreshJwt;

  private final JwtConfig jwtConfig;

  private final RedisService redisService;

  private final Decoder decoder = Base64.getUrlDecoder();

  private final ObjectMapper objectMapper;

  public JwtUtil(@Qualifier("accessJwt") Jwt accessJwt, @Qualifier("refreshJwt") Jwt refreshJwt, JwtConfig jwtConfig,
                 RedisService redisService, ObjectMapper objectMapper) {
    this.accessJwt = accessJwt;
    this.refreshJwt = refreshJwt;
    this.jwtConfig = jwtConfig;
    this.redisService = redisService;
    this.objectMapper = objectMapper;
  }

  public String getAccessToken(HttpServletRequest request) {
    String token = request.getHeader(jwtConfig.getAccessToken().getHeader());
    if(hasText(token)) {
      log.debug("Jwt authorization api detected: {}", token);
      return URLDecoder.decode(token, StandardCharsets.UTF_8);
    }
    return null;
  }

  public int getRefreshExpiry() {
    return refreshJwt.getExpirySeconds();
  }

  public String createAccessToken(Long userId, String email, List<GrantedAuthority> authorities) {
    String[] roles = authorities.stream()
        .map(GrantedAuthority::getAuthority)
        .toArray(String[]::new);
    return accessJwt.sign(new AccessClaim(userId, email, roles));
  }

  public String createRefreshToken(String email) {
    String refreshToken = refreshJwt.sign(new RefreshClaim(email));
    redisService.setValues(email, refreshToken, Duration.ofSeconds(
        refreshJwt.getExpirySeconds()));
    return refreshToken;
  }

  public void checkRefreshToken(String email, String refreshToken) {
    try{
      refreshJwt.verifyRefreshToken(refreshToken);
    } catch (TokenExpiredException e) {
      throw new AuthErrorException(TOKEN_EXPIRED);
    }
    String redisToken = (String) redisService.getValues(email);
    if(isNull(redisToken)) throw new AuthErrorException(REDIS_TOKEN_NOT_FOUND);
    if(!redisToken.equals(refreshToken)) {
      throw new AuthErrorException(INVALID_REFRESH_TOKEN_REQUEST);
    }
  }

  public void reissueAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String token = getAccessToken(request);
    AccessClaim claim = decodeExpiredAccessToken(token);
    String refreshToken = getRefreshTokenOfRequest(request);
    String accessToken = reissueAccessToken(claim, refreshToken);
    SignInResponse signInResponse = SignInResponse.from(claim.getUserId(), accessToken, refreshToken);
    ApiResponse apiResponse = ApiResponse.builder()
                    .message("토큰 재발행 성공")
                    .status(OK.value())
                    .data(signInResponse)
                    .build();
    response.setStatus(apiResponse.getStatus());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    String json = objectMapper.writeValueAsString(apiResponse);
    PrintWriter writer = response.getWriter();
    writer.write(json);
    writer.flush();
  }

  private String getRefreshTokenOfRequest(HttpServletRequest request) {
    if (isNull(request.getQueryString())) throw new AuthErrorException(INVALID_REFRESH_TOKEN_REQUEST);
    String[] chunks = request.getQueryString().split("=");
    if (chunks.length != 2) {
      throw new AuthErrorException(INVALID_REFRESH_TOKEN_REQUEST);
    }
    return chunks[1];
  }

  private String reissueAccessToken(AccessClaim claim, String refreshToken) {
    checkRefreshToken(claim.getEmail(), refreshToken);
    List<GrantedAuthority> authorities = getAuthorities(claim);
    return createAccessToken(claim.getUserId(), claim.getEmail(), authorities);
  }

  public void signOut(String token) {
    AccessClaim claim = accessJwt.verifyAccessToken(token);
    long expiredAccessTokenTime = claim.getExp().getTime() - new Date().getTime();
    redisService.setValues(jwtConfig.getBlackListPrefix() + token, claim.getEmail(), Duration.ofMillis(expiredAccessTokenTime));
    redisService.deleteValues(claim.getEmail());
  }

  public AccessClaim verifyAccessToken(String token) {
    String expiredAt = (String) redisService.getValues(jwtConfig.getBlackListPrefix() + token);
    if (expiredAt != null) throw new AuthErrorException(BLACKLIST_TOKEN_REQUEST);
    return accessJwt.verifyAccessToken(token);
  }

  public AccessClaim decodeExpiredAccessToken(String token) throws JsonProcessingException {
    String[] chunks = token.split("\\.");
    String payload = new String(decoder.decode(chunks[1]));
    return objectMapper.readValue(payload, AccessClaim.class);
  }

  List<GrantedAuthority> getAuthorities(AccessClaim claims) {
    String[] roles = claims.getRoles();
    return roles == null || roles.length == 0 ? Collections.emptyList() : Arrays.stream(roles).map(
            SimpleGrantedAuthority::new).collect(Collectors.toList());
  }
}
