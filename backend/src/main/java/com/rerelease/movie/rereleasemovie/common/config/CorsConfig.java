package com.rerelease.movie.rereleasemovie.common.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 개발 환경에서는 localhost 허용(배포 환경에서는 실제 URL로 변경)
        configuration.setAllowedOriginPatterns(List.of("http://localhost:3000"));

        // 허용할 HTTP 메서드를 설정
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));

        // 허용할 HTTP 요청 헤더를 설정 (Authorization, Content-Type)
        // 인증 요청 시 JWT 토큰은 Authorization 헤더를 사용하므로, 반드시 포함해야 함
        // Content-Type은 JSON 데이터 전송을 위한 필수 헤더
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        // 클라이언트가 인증 정보를 포함하여 요청을 보낼 수 있도록 허용하는 설정
        // true로 설정하면 쿠키 또는 Authorization 헤더가 포함된 요청을 허용함 (JWT 인증 시 필요)
        configuration.setAllowCredentials(true);

        // CORS 설정을 URL 패턴으로 매핑하기 위한 설정 객체 생성
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // 모든 경로(/**)에 대해 설정된 CORS 정책을 적용
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
