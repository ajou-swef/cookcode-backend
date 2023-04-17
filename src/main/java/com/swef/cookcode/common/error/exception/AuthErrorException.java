package com.swef.cookcode.common.error.exception;

import com.swef.cookcode.common.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class AuthErrorException extends AuthenticationException {

  private final ErrorCode errorCode;

  public AuthErrorException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
