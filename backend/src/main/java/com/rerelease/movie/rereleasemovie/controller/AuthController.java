package com.rerelease.movie.rereleasemovie.controller;

import com.rerelease.movie.rereleasemovie.dto.LoginRequestDto;
import com.rerelease.movie.rereleasemovie.dto.LoginResponseDto;
import com.rerelease.movie.rereleasemovie.dto.SignupRequestDto;
import com.rerelease.movie.rereleasemovie.dto.UserResponseDto;
import com.rerelease.movie.rereleasemovie.service.AuthService;
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

    /**
     * 회원가입 API
     *
     * @param request 회원가입 요청 DTO
     * @return 성공 메시지
     */
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody @Valid SignupRequestDto request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    /**
     * 로그인 API
     *
     * @param request 로그인 요청 DTO (이메일, 비밀번호)
     * @return JWT 토큰
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginUser(@RequestBody @Valid LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * 현재 로그인한 사용자 정보 조회 API
     *
     * @param userDetails 인증된 사용자 정보
     * @return 사용자 이메일
     */
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
