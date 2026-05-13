package com.rerelease.movie.rereleasemovie.auth.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final Key key;
    private static final long EXPIRATION_TIME = 1000 * 60 * 15; // 15분 (밀리초 단위)

    public JwtUtil(@Value("${secret.key}") String secretKey) {
        if (secretKey == null || secretKey.length() < 32) {
            throw new IllegalArgumentException("비밀키가 설정되지 않았거나 길이가 32바이트 미만입니다.");
        }
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(String email) {
        return Jwts.builder() // JWT 생성을 위한 JwtBuilder 반환
                   .setSubject(email) // 사용자 식별 정보 (subject)
                   .setIssuedAt(new Date()) // 토큰 발행 시간
                   .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 만료 시간
                   .signWith(key, SignatureAlgorithm.HS256) // 서명(Signature)
                   .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder() // JWT 검증을 위한 JwtParserBuilder 반환
                   .setSigningKey(key)
                   .build() // JwtParser 객체 생성
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true; // 유효한 토큰
        } catch (ExpiredJwtException e) {
            System.out.println("토큰이 만료되었습니다.");
        } catch (MalformedJwtException e) {
            System.out.println("잘못된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            System.out.println("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("JWT 토큰이 비어 있습니다.");
        }
        return false; // 유효하지 않은 토큰
    }
}
