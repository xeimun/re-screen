package com.rerelease.movie.rereleasemovie.auth.controller;

import com.rerelease.movie.rereleasemovie.auth.dto.LoginRequestDto;
import com.rerelease.movie.rereleasemovie.auth.dto.LoginResponseDto;
import com.rerelease.movie.rereleasemovie.auth.dto.SignupRequestDto;
import com.rerelease.movie.rereleasemovie.auth.dto.UserResponseDto;
import com.rerelease.movie.rereleasemovie.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody @Valid SignupRequestDto request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginUser(@RequestBody @Valid LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(authService.getCurrentUser(getUsername(userDetails)));
    }

    private String getUsername(UserDetails userDetails) {
        if (userDetails == null) {
            throw new AuthenticationCredentialsNotFoundException("로그인이 필요합니다.");
        }

        return userDetails.getUsername();
    }
}
