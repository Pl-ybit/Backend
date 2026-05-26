package com.example.playbit.user.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserProfileTest {

    @Test
    void createFor_전달한_user와_realName으로_생성된다() {
        User user = User.createLocal("test@example.com");
        UserProfile profile = UserProfile.createFor(user, "홍길동");

        assertThat(profile.getUser()).isEqualTo(user);
        assertThat(profile.getRealName()).isEqualTo("홍길동");
    }

    @Test
    void createFor_realName이_null이어도_생성된다() {
        User user = User.createLocal("test@example.com");
        UserProfile profile = UserProfile.createFor(user, null);

        assertThat(profile.getRealName()).isNull();
    }

    @Test
    void updateRealName_realName이_변경된다() {
        User user = User.createLocal("test@example.com");
        UserProfile profile = UserProfile.createFor(user, "홍길동");

        profile.updateRealName("김철수");

        assertThat(profile.getRealName()).isEqualTo("김철수");
    }
}
