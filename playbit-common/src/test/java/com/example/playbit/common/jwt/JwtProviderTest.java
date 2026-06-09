package com.example.playbit.common.jwt;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtProviderTest {

    JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        String secret = Base64.getEncoder().encodeToString(
                "test-secret-key-must-be-at-least-32-bytes!!".getBytes()
        );
        jwtProvider = new JwtProvider(secret, 3600000L);
    }

    @Test
    void 토큰을_생성한다() {
        String token = jwtProvider.generate(1L, "user@test.com", "USER", true);

        assertThat(token).isNotBlank();
    }

    @Test
    void 토큰에서_사용자_정보를_파싱한다() {
        String token = jwtProvider.generate(1L, "user@test.com", "USER", false);

        Claims claims = jwtProvider.parse(token);

        assertThat(claims.getSubject()).isEqualTo("1");
        assertThat(claims.get("email", String.class)).isEqualTo("user@test.com");
        assertThat(claims.get("role", String.class)).isEqualTo("USER");
        assertThat(claims.get("emailVerified", Boolean.class)).isFalse();
    }

    @Test
    void 유효한_토큰은_true를_반환한다() {
        String token = jwtProvider.generate(1L, "user@test.com", "USER", true);

        assertThat(jwtProvider.isValid(token)).isTrue();
    }

    @Test
    void 잘못된_토큰은_false를_반환한다() {
        assertThat(jwtProvider.isValid("invalid.token.value")).isFalse();
    }

    @Test
    void 변조된_토큰은_false를_반환한다() {
        String token = jwtProvider.generate(1L, "user@test.com", "USER", true);
        String tampered = token + "tampered";

        assertThat(jwtProvider.isValid(tampered)).isFalse();
    }
}
