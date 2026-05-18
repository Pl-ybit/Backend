package com.example.playbit.common.response;

import com.example.playbit.common.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    void ok_성공_응답을_반환한다() {
        ApiResponse<String> response = ApiResponse.ok("data");

        assertThat(response.success()).isTrue();
        assertThat(response.data()).isEqualTo("data");
        assertThat(response.message()).isNotBlank();
    }

    @Test
    void created_생성_성공_응답을_반환한다() {
        ApiResponse<Integer> response = ApiResponse.created(42);

        assertThat(response.success()).isTrue();
        assertThat(response.data()).isEqualTo(42);
    }

    @Test
    void fail_실패_응답은_success가_false이고_data가_null이다() {
        ApiResponse<Void> response = ApiResponse.fail(ErrorCode.INVALID_INPUT);

        assertThat(response.success()).isFalse();
        assertThat(response.data()).isNull();
        assertThat(response.message()).isEqualTo(ErrorCode.INVALID_INPUT.getMessage());
    }
}
