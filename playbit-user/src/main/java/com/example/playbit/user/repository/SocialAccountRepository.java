package com.example.playbit.user.repository;

import com.example.playbit.user.entity.SocialAccount;
import com.example.playbit.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

    Optional<SocialAccount> findByProviderAndProviderUserId(SocialAccount.Provider provider, String providerUserId);

    List<SocialAccount> findAllByUser(User user);
}
