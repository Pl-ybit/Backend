package com.example.playbit.user.entity;

import com.example.playbit.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_auths")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAuth extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    private String passwordHash;

    private LocalDateTime passwordUpdatedAt;

    @Column(nullable = false)
    private int loginFailCnt;

    private LocalDateTime lockedUntil;

    @Builder
    private UserAuth(User user, String passwordHash) {
        this.user = user;
        this.passwordHash = passwordHash;
        this.passwordUpdatedAt = LocalDateTime.now();
        this.loginFailCnt = 0;
    }

    public static UserAuth createFor(User user, String passwordHash) {
        return UserAuth.builder()
                .user(user)
                .passwordHash(passwordHash)
                .build();
    }

    public void incrementLoginFail() {
        this.loginFailCnt++;
    }

    public void lock(LocalDateTime until) {
        this.lockedUntil = until;
    }

    public void resetLoginFail() {
        this.loginFailCnt = 0;
        this.lockedUntil = null;
    }

    public boolean isLocked() {
        return lockedUntil != null && LocalDateTime.now().isBefore(lockedUntil);
    }

    public void updatePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
        this.passwordUpdatedAt = LocalDateTime.now();
    }
}
