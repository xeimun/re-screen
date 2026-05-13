package com.rerelease.movie.rereleasemovie.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rerelease.movie.rereleasemovie.auth.dto.LoginRequestDto;
import com.rerelease.movie.rereleasemovie.auth.dto.LoginResponseDto;
import com.rerelease.movie.rereleasemovie.auth.dto.SignupRequestDto;
import com.rerelease.movie.rereleasemovie.auth.dto.UserResponseDto;
import com.rerelease.movie.rereleasemovie.common.exception.EmailAlreadyRegisteredException;
import com.rerelease.movie.rereleasemovie.common.exception.UserNotFoundException;
import com.rerelease.movie.rereleasemovie.auth.entity.Users;
import com.rerelease.movie.rereleasemovie.auth.repository.UserRepository;
import com.rerelease.movie.rereleasemovie.auth.security.JwtUtil;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("인증 서비스")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(authenticationManager, userRepository, passwordEncoder, jwtUtil);
    }

    @Test
    @DisplayName("회원가입 시 비밀번호를 암호화하고 기본 사용자 권한으로 저장한다")
    void signupEncodesPasswordAndSavesUser() {
        SignupRequestDto request = createSignupRequest("user@example.com", "password123", "nickname");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");

        String message = authService.signup(request);

        assertThat(message).isEqualTo("회원가입 완료 :)");
        verify(userRepository).save(any(Users.class));
    }

    @Test
    @DisplayName("이미 가입된 이메일로 회원가입하면 예외가 발생한다")
    void signupRejectsDuplicateEmail() {
        SignupRequestDto request = createSignupRequest("user@example.com", "password123", "nickname");
        Users user = createUser("user@example.com", "encoded-password", "nickname");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(EmailAlreadyRegisteredException.class)
                .hasMessage("이미 가입된 이메일입니다.");

        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    @DisplayName("로그인 성공 시 JWT 토큰과 성공 메시지를 반환한다")
    void loginReturnsTokenWhenAuthenticationSucceeds() {
        LoginRequestDto request = createLoginRequest("user@example.com", "password123");
        Users user = createUser("user@example.com", "encoded-password", "nickname");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("user@example.com")).thenReturn("jwt-token");

        LoginResponseDto response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getMessage()).isEqualTo("로그인 성공");
    }

    @Test
    @DisplayName("비밀번호가 틀리면 토큰 없이 실패 메시지를 반환한다")
    void loginReturnsFailureMessageWhenPasswordIsWrong() {
        LoginRequestDto request = createLoginRequest("user@example.com", "wrong-password");
        Users user = createUser("user@example.com", "encoded-password", "nickname");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        LoginResponseDto response = authService.login(request);

        assertThat(response.getToken()).isNull();
        assertThat(response.getMessage()).isEqualTo("이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 로그인하면 사용자 없음 예외가 발생한다")
    void loginRejectsMissingUser() {
        LoginRequestDto request = createLoginRequest("missing@example.com", "password123");

        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("존재하지 않는 사용자입니다.");
    }

    @Test
    @DisplayName("현재 사용자 정보를 이메일과 닉네임으로 변환한다")
    void getCurrentUserReturnsUserResponse() {
        Users user = createUser("user@example.com", "encoded-password", "nickname");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        UserResponseDto response = authService.getCurrentUser("user@example.com");

        assertThat(response.getEmail()).isEqualTo("user@example.com");
        assertThat(response.getNickname()).isEqualTo("nickname");
    }

    @Test
    @DisplayName("현재 사용자 조회 시 사용자를 찾지 못하면 예외가 발생한다")
    void getCurrentUserRejectsMissingUser() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getCurrentUser("missing@example.com"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    private SignupRequestDto createSignupRequest(String email, String password, String nickname) {
        SignupRequestDto request = new SignupRequestDto();
        ReflectionTestUtils.setField(request, "email", email);
        ReflectionTestUtils.setField(request, "password", password);
        ReflectionTestUtils.setField(request, "nickname", nickname);
        return request;
    }

    private LoginRequestDto createLoginRequest(String email, String password) {
        LoginRequestDto request = new LoginRequestDto();
        ReflectionTestUtils.setField(request, "email", email);
        ReflectionTestUtils.setField(request, "password", password);
        return request;
    }

    private Users createUser(String email, String password, String nickname) {
        return Users.builder()
                    .email(email)
                    .password(password)
                    .nickname(nickname)
                    .emailVerified(false)
                    .role(Users.Role.ROLE_USER)
                    .build();
    }
}
