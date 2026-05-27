package com.example.playbit.user.service;

import com.example.playbit.common.exception.ErrorCode;
import com.example.playbit.common.exception.PlaybitException;
import com.example.playbit.user.dto.SignupRequest;
import com.example.playbit.user.dto.SignupResponse;
import com.example.playbit.user.dto.VerifyEmailRequest;
import com.example.playbit.user.entity.User;
import com.example.playbit.user.entity.UserAuth;
import com.example.playbit.user.entity.UserProfile;
import com.example.playbit.user.repository.UserAuthRepository;
import com.example.playbit.user.repository.UserProfileRepository;
import com.example.playbit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserAuthRepository userAuthRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new PlaybitException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = userRepository.save(User.createLocal(request.email()));
        userAuthRepository.save(UserAuth.createFor(user, passwordEncoder.encode(request.password())));
        userProfileRepository.save(UserProfile.createFor(user, request.realName()));

        emailVerificationService.sendVerificationCode(request.email());

        return new SignupResponse(user.getId(), user.getEmail());
    }

    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        emailVerificationService.verify(request.email(), request.code());

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new PlaybitException(ErrorCode.RESOURCE_NOT_FOUND));
        user.verifyEmail();
    }
}
