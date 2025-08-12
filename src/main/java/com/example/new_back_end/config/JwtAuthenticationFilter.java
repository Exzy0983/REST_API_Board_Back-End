package com.example.new_back_end.config;

import com.example.new_back_end.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT 인증 필터 클래스
 * - 모든 HTTP 요청을 가로채서 JWT 토큰을 검증
 * - 유효한 토큰이 있으면 SecurityContext에 인증 정보 설정
 * - OncePerRequestFilter를 상속하여 요청당 한 번만 실행되도록 보장
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    /**
     * 필터의 핵심 로직
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param filterChain 필터 체인
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        // 1. HTTP 헤더에서 JWT 토큰 추출
        String token = jwtUtil.extractTokenFromRequest(request);
        
        // 2. 토큰이 존재하고 현재 SecurityContext에 인증 정보가 없는 경우
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            try {
                // 3. 토큰에서 사용자명 추출
                String username = jwtUtil.getUsernameFromToken(token);
                
                // 4. 토큰 유효성 검증
                if (jwtUtil.validateToken(token, username)) {
                    
                    // 5. 인증 토큰 생성 (Spring Security의 Authentication 객체)
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            username,           // 주체(사용자명)
                            null,              // 자격증명(비밀번호는 null로 설정)
                            new ArrayList<>()  // 권한 목록(현재는 빈 목록)
                        );
                    
                    // 6. 요청 세부 정보 설정
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 7. SecurityContext에 인증 정보 설정
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                // 토큰 처리 중 예외 발생 시 로그 출력 (실제 운영에서는 로거 사용)
                System.err.println("JWT 토큰 처리 중 오류 발생: " + e.getMessage());
            }
        }
        
        // 8. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}