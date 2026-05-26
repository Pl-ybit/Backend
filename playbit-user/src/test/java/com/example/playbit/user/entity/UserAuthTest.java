package com.example.playbit.user.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserAuthTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = User.createLocal("test@example.com");
    }

    @Test
    void createFor_초기_loginFailCnt는_0이다() {
        UserAuth auth = UserAuth.createFor(user, "hashedPw");
        assertThat(auth.getLoginFailCnt()).isZero();
    }

    @Test
    void createFor_초기_lockedUntil은_null이다() {
        UserAuth auth = UserAuth.createFor(user, "hashedPw");
        assertThat(auth.getLockedUntil()).isNull();
    }

    @Test
    void createFor_passwordUpdatedAt이_설정된다() {
        LocalDateTime before = LocalDateTime.now();
        UserAuth auth = UserAuth.createFor(user, "hashedPw");
        assertThat(auth.getPasswordUpdatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    void incrementLoginFail_호출마다_loginFailCnt가_1씩_증가한다() {
        UserAuth auth = UserAuth.createFor(user, "hashedPw");
        auth.incrementLoginFail();
        auth.incrementLoginFail();
        assertThat(auth.getLoginFailCnt()).isEqualTo(2);
    }

    @Test
    void isLocked_lockedUntil이_null이면_false() {
        UserAuth auth = UserAuth.createFor(user, "hashedPw");
        assertThat(auth.isLocked()).isFalse();
    }

    @Test
    void isLocked_lockedUntil이_미래면_true() {
        UserAuth auth = UserAuth.createFor(user, "hashedPw");
        auth.lock(LocalDateTime.now().plusHours(1));
        assertThat(auth.isLocked()).isTrue();
    }

    @Test
    void isLocked_lockedUntil이_과거면_false() {
        UserAuth auth = UserAuth.createFor(user, "hashedPw");
        auth.lock(LocalDateTime.now().minusSeconds(1));
        assertThat(auth.isLocked()).isFalse();
    }

    @Test
    void resetLoginFail_loginFailCnt와_lockedUntil이_초기화된다() {
        UserAuth auth = UserAuth.createFor(user, "hashedPw");
        auth.incrementLoginFail();
        auth.lock(LocalDateTime.now().plusHours(1));

        auth.resetLoginFail();

        assertThat(auth.getLoginFailCnt()).isZero();
        assertThat(auth.getLockedUntil()).isNull();
    }

    @Test
    void updatePassword_passwordHash와_passwordUpdatedAt이_갱신된다() {
        UserAuth auth = UserAuth.createFor(user, "oldHash");
        LocalDateTime before = auth.getPasswordUpdatedAt();

        auth.updatePassword("newHash");

        assertThat(auth.getPasswordHash()).isEqualTo("newHash");
        assertThat(auth.getPasswordUpdatedAt()).isAfterOrEqualTo(before);
    }
}
