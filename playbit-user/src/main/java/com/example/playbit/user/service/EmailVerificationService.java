package com.example.playbit.user.service;

import com.example.playbit.common.exception.ErrorCode;
import com.example.playbit.common.exception.PlaybitException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final String KEY_PREFIX = "email:verify:";
    private static final Duration TTL = Duration.ofMinutes(5);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final RedisTemplate<String, String> redisTemplate;
    private final JavaMailSender mailSender;

    public void sendVerificationCode(String email) {
        String code = generateCode();
        redisTemplate.opsForValue().set(KEY_PREFIX + email, code, TTL);
        sendEmail(email, code);
    }

    public void verify(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(KEY_PREFIX + email);
        if (storedCode == null || !storedCode.equals(code)) {
            throw new PlaybitException(ErrorCode.INVALID_VERIFICATION_CODE);
        }
        redisTemplate.delete(KEY_PREFIX + email);
    }

    private String generateCode() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }

    private void sendEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[Playbit] 이메일 인증 코드");
        message.setText("인증 코드: " + code + "\n\n5분 내에 입력해 주세요.");
        mailSender.send(message);
    }
}
