package com.example.playbit.user.entity;

import com.example.playbit.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    private String realName;

    @Builder
    private UserProfile(User user, String realName) {
        this.user = user;
        this.realName = realName;
    }

    public static UserProfile createFor(User user, String realName) {
        return UserProfile.builder()
                .user(user)
                .realName(realName)
                .build();
    }

    public void updateRealName(String realName) {
        this.realName = realName;
    }
}
