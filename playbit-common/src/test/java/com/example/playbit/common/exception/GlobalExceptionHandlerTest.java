package com.example.playbit.common.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void PlaybitException_발생_시_ErrorResponse와_적절한_상태코드를_반환한다() throws Exception {
        mockMvc.perform(get("/test/playbit-exception"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.RESOURCE_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.RESOURCE_NOT_FOUND.getMessage()));
    }

    @Test
    void 처리되지않은_예외_발생_시_500을_반환한다() throws Exception {
        mockMvc.perform(get("/test/server-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(ErrorCode.INTERNAL_SERVER_ERROR.getCode()));
    }

    @Test
    void 유효성_검사_실패_시_400을_반환한다() throws Exception {
        String invalidBody = "{\"name\": \"\"}";

        mockMvc.perform(post("/test/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_INPUT.getCode()));
    }

    @RestController
    static class TestController {

        @GetMapping("/test/playbit-exception")
        void throwPlaybitException() {
            throw new PlaybitException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        @GetMapping("/test/server-error")
        void throwServerError() {
            throw new RuntimeException("unexpected");
        }

        @PostMapping("/test/validate")
        void validate(@Valid @RequestBody ValidRequest request) {}

        record ValidRequest(@NotBlank String name) {}
    }
}
