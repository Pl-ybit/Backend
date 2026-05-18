package com.example.playbit.common.exception;

public class PlaybitException extends RuntimeException {

    private final ErrorCode errorCode;

    public PlaybitException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
