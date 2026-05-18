package com.example.playbit.common.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class PlaybitExceptionTest {

    @Test
    void ErrorCode로_예외를_생성한다() {
        PlaybitException ex = new PlaybitException(ErrorCode.RESOURCE_NOT_FOUND);

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        assertThat(ex.getMessage()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND.getMessage());
    }

    @Test
    void ErrorCode의_HttpStatus를_반환한다() {
        PlaybitException ex = new PlaybitException(ErrorCode.UNAUTHORIZED);

        assertThat(ex.getErrorCode().getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
