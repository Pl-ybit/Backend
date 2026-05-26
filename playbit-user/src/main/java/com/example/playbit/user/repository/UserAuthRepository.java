package com.example.playbit.user.repository;

import com.example.playbit.user.entity.User;
import com.example.playbit.user.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {

    Optional<UserAuth> findByUser(User user);
}
