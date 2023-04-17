package com.swef.cookcode.common.error.exception;

import com.swef.cookcode.common.ErrorCode;
import lombok.Getter;

@Getter
public class InvalidRequestException extends RuntimeException {

  private final ErrorCode errorCode;
  public InvalidRequestException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
