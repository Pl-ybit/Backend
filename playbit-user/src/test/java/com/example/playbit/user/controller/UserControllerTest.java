package com.example.playbit.user.controller;

import com.example.playbit.common.exception.ErrorCode;
import com.example.playbit.common.exception.GlobalExceptionHandler;
import com.example.playbit.common.exception.PlaybitException;
import com.example.playbit.user.dto.SignupRequest;
import com.example.playbit.user.dto.SignupResponse;
import com.example.playbit.user.dto.VerifyEmailRequest;
import com.example.playbit.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock private UserService userService;
    @InjectMocks private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("정상 요청 시 201 Created와 ApiResponse를 반환한다")
    void signup_성공_201() throws Exception {
        SignupRequest request = new SignupRequest("test@example.com", "password123", "홍길동");
        SignupResponse response = new SignupResponse(1L, "test@example.com");
        given(userService.signup(any())).willReturn(response);

        mockMvc.perform(post("/api/v1/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    @Test
    @DisplayName("이메일 중복 시 409 Conflict와 ErrorResponse를 반환한다")
    void signup_이메일중복_409() throws Exception {
        SignupRequest request = new SignupRequest("dup@example.com", "password123", "홍길동");
        given(userService.signup(any())).willThrow(new PlaybitException(ErrorCode.DUPLICATE_EMAIL));

        mockMvc.perform(post("/api/v1/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("U001"));
    }

    @Test
    @DisplayName("@Valid 실패 시 400 Bad Request와 ErrorResponse를 반환한다")
    void signup_유효성실패_400() throws Exception {
        SignupRequest request = new SignupRequest("invalid-email", "short", "");

        mockMvc.perform(post("/api/v1/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("C001"));
    }

    @Test
    @DisplayName("이메일 인증 성공 시 200 OK와 ApiResponse를 반환한다")
    void verifyEmail_성공_200() throws Exception {
        VerifyEmailRequest request = new VerifyEmailRequest("test@example.com", "123456");

        mockMvc.perform(post("/api/v1/users/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("인증 코드 불일치 시 400 Bad Request와 ErrorResponse를 반환한다")
    void verifyEmail_코드불일치_400() throws Exception {
        VerifyEmailRequest request = new VerifyEmailRequest("test@example.com", "000000");
        willThrow(new PlaybitException(ErrorCode.INVALID_VERIFICATION_CODE))
                .given(userService).verifyEmail(any());

        mockMvc.perform(post("/api/v1/users/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("U002"));
    }
}
