package com.example.playbit.user.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SocialAccountTest {

    @Test
    void link_전달한_값으로_소셜계정이_생성된다() {
        User user = User.createSocial("test@gmail.com");
        LocalDateTime before = LocalDateTime.now();

        SocialAccount account = SocialAccount.link(user, SocialAccount.Provider.GOOGLE, "google-uid-123", "test@gmail.com");

        assertThat(account.getUser()).isEqualTo(user);
        assertThat(account.getProvider()).isEqualTo(SocialAccount.Provider.GOOGLE);
        assertThat(account.getProviderUserId()).isEqualTo("google-uid-123");
        assertThat(account.getEmail()).isEqualTo("test@gmail.com");
        assertThat(account.getLinkedAt()).isAfterOrEqualTo(before);
    }

    @Test
    void link_소셜_이메일이_null이어도_생성된다() {
        User user = User.createSocial("test@kakao.com");

        SocialAccount account = SocialAccount.link(user, SocialAccount.Provider.KAKAO, "kakao-uid-456", null);

        assertThat(account.getEmail()).isNull();
    }
}
