package com.swef.cookcode.common.filter;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swef.cookcode.common.ApiResponse;
import com.swef.cookcode.common.ErrorCode;
import com.swef.cookcode.common.ErrorResponse;
import com.swef.cookcode.common.error.exception.AuthErrorException;
import com.swef.cookcode.common.jwt.claims.AccessClaim;
import com.swef.cookcode.common.jwt.JwtUtil;
import com.swef.cookcode.user.service.UserSimpleService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

  private final ObjectMapper objectMapper;

  private final String tokenReissuePath = "/api/v1/account/token/reissue";

  private final JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (TokenExpiredException e) {
      if (request.getServletPath().equals(tokenReissuePath)) {
        jwtUtil.reissueAccessToken(request, response);
        return;
      }
      setErrorResponse(HttpStatus.UNAUTHORIZED, response, ErrorCode.TOKEN_EXPIRED);
    } catch (AuthErrorException e) {
      setErrorResponse(HttpStatus.BAD_REQUEST, response, ErrorCode.BLACKLIST_TOKEN_REQUEST);
    } catch (SignatureVerificationException e) {
      setErrorResponse(HttpStatus.UNAUTHORIZED, response, ErrorCode.INVALID_TOKEN_SIGN);
    } catch (RuntimeException e) {
      log.warn(e.getMessage());
      setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, response, ErrorCode.INTERNAL_SERVER_ERROR);
    }
  }

  public void setErrorResponse(HttpStatus status, HttpServletResponse response, ErrorCode errorCode) {
    response.setStatus(status.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    try {
      String json = objectMapper.writeValueAsString(ErrorResponse.of(errorCode));
      PrintWriter writer = response.getWriter();
      writer.write(json);
      writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
