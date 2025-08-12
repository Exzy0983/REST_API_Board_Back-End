package com.example.new_back_end.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

    /**
     * HTTP 요청에서 JWT 토큰을 추출하는 메서드
     * Authorization 헤더에서 "Bearer " 접두사를 제거하고 토큰만 추출
     * @param request HTTP 요청 객체
     * @return JWT 토큰 문자열 (토큰이 없으면 null 반환)
     */
    public String extractTokenFromRequest(HttpServletRequest request) {
        // 1. Authorization 헤더에서 값 추출
        String bearerToken = request.getHeader("Authorization");
        
        // 2. Bearer 토큰 형식 확인 및 토큰 추출
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // "Bearer " 접두사(7글자)를 제거하고 실제 토큰만 반환
            return bearerToken.substring(7);
        }
        
        return null;  // 토큰이 없거나 형식이 올바르지 않은 경우
    }

    /**
     * JWT 토큰의 만료 시간을 반환하는 메서드
     * @param token JWT 토큰
     * @return 토큰 만료 시간 (Date 객체)
     */
    public Date getExpirationFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
}
