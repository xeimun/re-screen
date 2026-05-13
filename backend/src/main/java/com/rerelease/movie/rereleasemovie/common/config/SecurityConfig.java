package com.rerelease.movie.rereleasemovie.common.config;

import com.rerelease.movie.rereleasemovie.auth.security.JwtAuthenticationFilter;
import com.rerelease.movie.rereleasemovie.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보안 비활성화 (API 개발 시 필요)
                .cors(cors -> cors.configurationSource(corsConfigurationSource)) // Spring Security 필터 체인에 CORS 설정 적용
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS)) // 세션 사용 안 함(JWT 인증 방식 사용)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/kofic/**", "/api/tmdb/**")
                        .permitAll() // 회원가입, 로그인, Open API 요청은 인증 없이 허용
                        .anyRequest()
                        .authenticated() // 그 외 모든 요청은 인증 필요
                )
                // JWT 인증 방식에서는 UsernamePasswordAuthenticationFilter를 사용하지 않으므로, JWT 인증 필터를 먼저 실행해야 함
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
