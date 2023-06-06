package com.swef.cookcode.common.filter;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swef.cookcode.common.ApiResponse;
import com.swef.cookcode.common.ErrorCode;
import com.swef.cookcode.common.ErrorResponse;
import com.swef.cookcode.common.error.exception.AuthErrorException;
import com.swef.cookcode.common.jwt.JwtUtil;
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
@SuppressWarnings({"rawtypes"})
public class ExceptionHandlerFilter extends OncePerRequestFilter {

  private final String tokenReissuePath = "/api/v1/account/token/reissue";

  private final JwtUtil jwtUtil;

  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (TokenExpiredException e) {
      if (request.getServletPath().equals(tokenReissuePath)) {
        ApiResponse apiResponse = jwtUtil.reissueAccessToken(request);
        setResponse(apiResponse.getStatus(), response, apiResponse);
        return;
      }
      setResponse(HttpStatus.UNAUTHORIZED.value(), response, ErrorResponse.of(ErrorCode.TOKEN_EXPIRED));
    } catch (AuthErrorException e) {
      setResponse(HttpStatus.BAD_REQUEST.value(), response, ErrorResponse.of(ErrorCode.BLACKLIST_TOKEN_REQUEST));
    } catch (SignatureVerificationException e) {
      setResponse(HttpStatus.UNAUTHORIZED.value(), response, ErrorResponse.of(ErrorCode.INVALID_TOKEN_SIGN));
    } catch (RuntimeException e) {
      log.warn(e.getMessage());
      setResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), response, ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
    }
  }

  public void setResponse(int status, HttpServletResponse response, Object responseBody) {
    response.setStatus(status);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    try {
      String json = objectMapper.writeValueAsString(responseBody);
      PrintWriter writer = response.getWriter();
      writer.write(json);
      writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
