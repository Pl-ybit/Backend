package com.example.playbit.user.service;

import com.example.playbit.common.exception.ErrorCode;
import com.example.playbit.common.exception.PlaybitException;
import com.example.playbit.user.dto.SignupRequest;
import com.example.playbit.user.dto.SignupResponse;
import com.example.playbit.user.dto.VerifyEmailRequest;
import com.example.playbit.user.entity.User;
import com.example.playbit.user.repository.UserAuthRepository;
import com.example.playbit.user.repository.UserProfileRepository;
import com.example.playbit.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserAuthRepository userAuthRepository;
    @Mock private UserProfileRepository userProfileRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailVerificationService emailVerificationService;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("정상 요청 시 User, UserAuth, UserProfile을 저장하고 인증 이메일을 발송한다")
    void signup_성공() {
        SignupRequest request = new SignupRequest("test@example.com", "password123", "홍길동");

        given(userRepository.existsByEmail("test@example.com")).willReturn(false);
        given(passwordEncoder.encode("password123")).willReturn("hashed_password");
        given(userRepository.save(any(User.class))).willAnswer(inv -> inv.getArgument(0));

        SignupResponse response = userService.signup(request);

        assertThat(response.email()).isEqualTo("test@example.com");
        verify(userAuthRepository).save(any());
        verify(userProfileRepository).save(any());
        verify(emailVerificationService).sendVerificationCode("test@example.com");
    }

    @Test
    @DisplayName("이메일이 중복되면 DUPLICATE_EMAIL 예외를 던진다")
    void signup_이메일중복_예외() {
        SignupRequest request = new SignupRequest("dup@example.com", "password123", "홍길동");
        given(userRepository.existsByEmail("dup@example.com")).willReturn(true);

        assertThatThrownBy(() -> userService.signup(request))
                .isInstanceOf(PlaybitException.class)
                .satisfies(e -> assertThat(((PlaybitException) e).getErrorCode())
                        .isEqualTo(ErrorCode.DUPLICATE_EMAIL));

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("올바른 인증 코드 입력 시 emailVerified가 true로 변경된다")
    void verifyEmail_성공() {
        VerifyEmailRequest request = new VerifyEmailRequest("test@example.com", "123456");
        User user = User.createLocal("test@example.com");

        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(user));

        userService.verifyEmail(request);

        assertThat(user.isEmailVerified()).isTrue();
        verify(emailVerificationService).verify("test@example.com", "123456");
    }

    @Test
    @DisplayName("인증 대상 이메일의 유저가 없으면 RESOURCE_NOT_FOUND 예외를 던진다")
    void verifyEmail_유저없음_예외() {
        VerifyEmailRequest request = new VerifyEmailRequest("none@example.com", "123456");
        given(userRepository.findByEmail("none@example.com")).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.verifyEmail(request))
                .isInstanceOf(PlaybitException.class)
                .satisfies(e -> assertThat(((PlaybitException) e).getErrorCode())
                        .isEqualTo(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
