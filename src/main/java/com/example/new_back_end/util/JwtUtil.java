package com.example.new_back_end.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long expiration;

    // JWT 토큰 생성
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)  // 토큰 주체 (사용자명)
                .setIssuedAt(new Date())  // 토큰 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + expiration))  // 토큰 만료 시간
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 서명 알고리즘과 비밀키
                .compact();  // 토큰 생성
    }

    // JWT 토큰에서 사용자명 추출
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)  // 비밀키로 검증
                .parseClaimsJws(token)     // 토큰 파싱
                .getBody();                // Claims 추출
        return claims.getSubject();        // 사용자명 반환
    }

    // JWT 토큰 유효성 검증
    public boolean validateToken(String token, String username) {
        try {
            String tokenUsername = getUsernameFromToken(token);
            return tokenUsername.equals(username) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;  // 토큰이 유효하지 않음
        }
    }

    // 토큰 만료 여부 확인
    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());  // 현재 시간보다 이전이면 만료
    }
}
