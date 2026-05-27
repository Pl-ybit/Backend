package com.example.playbit.user.service;

import com.example.playbit.common.exception.ErrorCode;
import com.example.playbit.common.exception.PlaybitException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.time.Duration;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailVerificationService emailVerificationService;

    @Test
    @DisplayName("인증 코드 발송 시 Redis에 저장하고 이메일을 전송한다")
    void sendVerificationCode_성공() {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        emailVerificationService.sendVerificationCode("test@example.com");

        verify(valueOperations).set(eq("email:verify:test@example.com"), anyString(), any(Duration.class));
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("올바른 인증 코드 검증 시 Redis 키를 삭제한다")
    void verifyCode_성공() {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get("email:verify:test@example.com")).willReturn("123456");

        emailVerificationService.verify("test@example.com", "123456");

        verify(redisTemplate).delete("email:verify:test@example.com");
    }

    @Test
    @DisplayName("인증 코드가 만료되거나 없으면 예외를 던진다")
    void verifyCode_코드만료_예외() {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get("email:verify:test@example.com")).willReturn(null);

        assertThatThrownBy(() -> emailVerificationService.verify("test@example.com", "123456"))
                .isInstanceOf(PlaybitException.class)
                .satisfies(e -> {
                    PlaybitException ex = (PlaybitException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_VERIFICATION_CODE);
                });
    }

    @Test
    @DisplayName("인증 코드가 일치하지 않으면 예외를 던진다")
    void verifyCode_코드불일치_예외() {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get("email:verify:test@example.com")).willReturn("999999");

        assertThatThrownBy(() -> emailVerificationService.verify("test@example.com", "123456"))
                .isInstanceOf(PlaybitException.class)
                .satisfies(e -> {
                    PlaybitException ex = (PlaybitException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_VERIFICATION_CODE);
                });
    }
}
