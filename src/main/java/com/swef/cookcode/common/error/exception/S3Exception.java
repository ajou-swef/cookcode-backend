package com.swef.cookcode.common.error.exception;

import com.swef.cookcode.common.ErrorCode;

public class S3Exception  extends RuntimeException{

    private final ErrorCode errorCode;

    public S3Exception(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
