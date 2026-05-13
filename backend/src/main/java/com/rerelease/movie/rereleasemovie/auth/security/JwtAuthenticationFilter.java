package com.rerelease.movie.rereleasemovie.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService; // 직접 구현한 CustomUserDetailsService가 자동 주입됨 (의존성 주입 + 다형성)

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 1. HTTP 요청에서 Authorization 헤더 가져오기
        String authHeader = request.getHeader("Authorization");

        // 2. 헤더가 없거나 "Bearer "로 시작하지 않으면 필터 통과
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // 3. "Bearer " 이후의 토큰 값만 추출
        String token = authHeader.substring(7);

        try {
            // 4. JWT에서 사용자 이메일(Subject) 추출
            String email = jwtUtil.extractUsername(token);

            // 5. SecurityContext에 인증되지 않은 상태라면 인증 진행
            if (email != null && SecurityContextHolder.getContext()
                                                      .getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // 6. JWT 유효성 검사
                if (jwtUtil.validateToken(token)) {
                    // 7. 사용자 인증 객체 생성 및 SecurityContext에 저장
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext()
                                         .setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            System.out.println("JWT 인증 중 오류 발생: " + e.getMessage());
        }

        // 8. 다음 필터 실행
        chain.doFilter(request, response);
    }
}
