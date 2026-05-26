package com.example.playbit.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "social_accounts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_user_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialAccount {

    public enum Provider {
        GOOGLE, KAKAO, NAVER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Provider provider;

    @Column(nullable = false)
    private String providerUserId;

    private String email;

    private LocalDateTime linkedAt;

    @Builder
    private SocialAccount(User user, Provider provider, String providerUserId, String email) {
        this.user = user;
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.email = email;
        this.linkedAt = LocalDateTime.now();
    }

    public static SocialAccount link(User user, Provider provider, String providerUserId, String email) {
        return SocialAccount.builder()
                .user(user)
                .provider(provider)
                .providerUserId(providerUserId)
                .email(email)
                .build();
    }
}
