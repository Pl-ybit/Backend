package com.example.playbit.user.controller;

import com.example.playbit.common.response.ApiResponse;
import com.example.playbit.user.dto.SignupRequest;
import com.example.playbit.user.dto.SignupResponse;
import com.example.playbit.user.dto.VerifyEmailRequest;
import com.example.playbit.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ApiResponse.created(userService.signup(request));
    }

    @PostMapping("/verify-email")
    public ApiResponse<Void> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        userService.verifyEmail(request);
        return ApiResponse.ok(null);
    }
}
