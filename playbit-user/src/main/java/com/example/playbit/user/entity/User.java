package com.example.playbit.user.entity;

import com.example.playbit.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@org.hibernate.annotations.SQLDelete(sql = "UPDATE users SET deleted_at = now() WHERE id = ?")
@org.hibernate.annotations.SQLRestriction("deleted_at IS NULL")
public class User extends BaseTimeEntity {

    public enum Role {
        USER, ADMIN
    }

    public enum Status {
        ACTIVE,    // 정상
        DORMANT,   // 휴면
        SUSPENDED, // 제재
        WITHDRAWN  // 탈퇴
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @Column(nullable = false)
    private boolean emailVerified;

    private LocalDateTime lastLoginAt;

    private LocalDateTime deletedAt;

    @Builder
    private User(String email, Role role, Status status, boolean emailVerified) {
        this.email = email;
        this.role = role;
        this.status = status;
        this.emailVerified = emailVerified;
    }

    public static User createLocal(String email) {
        return User.builder()
                .email(email)
                .role(Role.USER)
                .status(Status.ACTIVE)
                .emailVerified(false)
                .build();
    }

    public static User createSocial(String email) {
        return User.builder()
                .email(email)
                .role(Role.USER)
                .status(Status.ACTIVE)
                .emailVerified(true)
                .build();
    }

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void suspend() {
        this.status = Status.SUSPENDED;
    }

    public void withdraw() {
        this.status = Status.WITHDRAWN;
    }
}
