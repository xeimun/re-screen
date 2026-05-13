package com.rerelease.movie.rereleasemovie.auth.service;

import com.rerelease.movie.rereleasemovie.auth.dto.LoginRequestDto;
import com.rerelease.movie.rereleasemovie.auth.dto.LoginResponseDto;
import com.rerelease.movie.rereleasemovie.auth.dto.SignupRequestDto;
import com.rerelease.movie.rereleasemovie.auth.dto.UserResponseDto;
import com.rerelease.movie.rereleasemovie.common.exception.EmailAlreadyRegisteredException;
import com.rerelease.movie.rereleasemovie.common.exception.UserNotFoundException;
import com.rerelease.movie.rereleasemovie.auth.entity.Users;
import com.rerelease.movie.rereleasemovie.auth.repository.UserRepository;
import com.rerelease.movie.rereleasemovie.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String signup(SignupRequestDto request) {
        if (userRepository.findByEmail(request.getEmail())
                          .isPresent()) {
            throw new EmailAlreadyRegisteredException("이미 가입된 이메일입니다.");
        }

        Users newUser = Users.builder()
                             .email(request.getEmail())
                             .password(passwordEncoder.encode(request.getPassword()))
                             .nickname(request.getNickname())
                             .emailVerified(false)
                             .role(Users.Role.ROLE_USER)
                             .build();

        userRepository.save(newUser);
        return "회원가입 완료 :)";
    }

    public LoginResponseDto login(LoginRequestDto request) {
        if (userRepository.findByEmail(request.getEmail())
                          .isEmpty()) {
            throw new UserNotFoundException("존재하지 않는 사용자입니다.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            return LoginResponseDto.builder()
                                   .token(null)
                                   .message("이메일 또는 비밀번호가 올바르지 않습니다.")
                                   .build();
        }

        return LoginResponseDto.builder()
                               .token(jwtUtil.generateToken(request.getEmail()))
                               .message("로그인 성공")
                               .build();
    }

    public UserResponseDto getCurrentUser(String email) {
        Users user = userRepository.findByEmail(email)
                                   .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        return UserResponseDto.builder()
                              .email(user.getEmail())
                              .nickname(user.getNickname())
                              .build();
    }
}
