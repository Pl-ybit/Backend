package com.example.playbit.common.response;

import com.example.playbit.common.exception.ErrorCode;

public record ApiResponse<T>(boolean success, String message, T data) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "요청이 처리되었습니다.", data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(true, "리소스가 생성되었습니다.", data);
    }

    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getMessage(), null);
    }
}
