package com.swef.cookcode.common.jwt;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.swef.cookcode.common.ApiResponse;
import com.swef.cookcode.common.ErrorCode;
import com.swef.cookcode.common.error.exception.AuthErrorException;
import com.swef.cookcode.common.error.exception.NotFoundException;
import com.swef.cookcode.common.jwt.claims.AccessClaim;
import com.swef.cookcode.common.util.Util;
import com.swef.cookcode.user.domain.User;
import com.swef.cookcode.user.service.UserSimpleService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
@SuppressWarnings({"rawtypes"})
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtUtil jwtUtil;

  private final Util util;

  private final UserSimpleService userSimpleService;

  private final String tokenReissuePath = "/api/v1/account/token/reissue";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain chain) throws ServletException, IOException {
    if (nonNull(SecurityContextHolder.getContext().getAuthentication())) {
      log.debug("SecurityContextHolder는 이미 authentication 객체를 가지고 있습니다.: '{}'", SecurityContextHolder.getContext().getAuthentication());
      chain.doFilter(request, response);
      return;
    }
    String token = jwtUtil.getAccessToken(request);
    if (isNull(token)) {
      chain.doFilter(request, response);
      return;
    }
    try {
      AccessClaim claims = jwtUtil.verifyAccessToken(token);
      if (request.getServletPath().equals(tokenReissuePath)) throw new AuthErrorException(ErrorCode.TOKEN_NOT_EXPIRED);
      Long userId = claims.getUserId();
      List<GrantedAuthority> authorities = jwtUtil.getAuthorities(claims);
      User currentUser = userSimpleService.getUserById(userId);
      if (!isNull(userId) && authorities.size() > 0) {
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(new JwtPrincipal(token, currentUser), null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (NotFoundException e) {
      log.warn("탈퇴한 유저의 토큰입니다. token: {}", token);
      throw e;
    } catch (TokenExpiredException e) {
      log.warn("토큰이 만료된 요청입니다. token: {}", token);
      if (!request.getServletPath().equals(tokenReissuePath)) throw e;
      ApiResponse apiResponse = jwtUtil.reissueAccessToken(request);
      util.setResponse(apiResponse.getStatus(), response, apiResponse);
      return;
    } catch (AuthErrorException e) {
      log.warn("token: {}", token);
      throw e;
    } catch (Exception e) {
      log.warn("Jwt 처리 실패: {}, class: {}", e.getMessage(), e.getClass());
      throw e;
    }
    chain.doFilter(request, response);
  }
}
