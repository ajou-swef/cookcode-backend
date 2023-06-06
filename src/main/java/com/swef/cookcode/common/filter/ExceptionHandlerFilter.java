package com.swef.cookcode.common.filter;

import static com.swef.cookcode.common.ErrorCode.INVALID_TOKEN_PROCESSING;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.swef.cookcode.common.ErrorCode;
import com.swef.cookcode.common.ErrorResponse;
import com.swef.cookcode.common.error.exception.AuthErrorException;
import com.swef.cookcode.common.util.Util;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings({"rawtypes"})
public class ExceptionHandlerFilter extends OncePerRequestFilter {

  private final Util util;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (TokenExpiredException e) {
      util.setResponse(HttpStatus.UNAUTHORIZED.value(), response, ErrorResponse.of(ErrorCode.TOKEN_EXPIRED));
    } catch (AuthErrorException e) {
      log.warn(e.getMessage());
      util.setResponse(HttpStatus.BAD_REQUEST.value(), response, ErrorResponse.of(e.getErrorCode()));
    } catch(JsonProcessingException e) {
      log.warn(e.getMessage());
      util.setResponse(HttpStatus.BAD_REQUEST.value(), response, ErrorResponse.of(INVALID_TOKEN_PROCESSING));
    } catch (SignatureVerificationException e) {
      util.setResponse(HttpStatus.UNAUTHORIZED.value(), response, ErrorResponse.of(ErrorCode.INVALID_TOKEN_SIGN));
    } catch (RuntimeException e) {
      log.warn(e.getMessage());
      util.setResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), response, ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
    }
  }

}
