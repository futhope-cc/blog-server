package com.cc.blogserver.exception;

import lombok.Getter;

/**
 * 业务异常
 * 使用: throw new BusinessException(ErrorCode.USER_NOT_EXIST);
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
