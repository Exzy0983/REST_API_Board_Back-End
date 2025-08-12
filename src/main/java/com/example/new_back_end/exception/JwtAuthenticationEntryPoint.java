package com.example.new_back_end.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT 인증 실패 시 처리하는 진입점 클래스
 * - 인증되지 않은 사용자가 보호된 리소스에 접근할 때 호출
 * - 401 Unauthorized 응답을 반환
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 인증 실패 시 호출되는 메서드
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param authException 인증 예외
     */
    @Override
    public void commence(HttpServletRequest request,
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {
        
        // 1. 응답 상태 코드를 401 Unauthorized로 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        // 2. 응답 컨텐츠 타입을 JSON으로 설정
        response.setContentType("application/json;charset=UTF-8");
        
        // 3. 에러 메시지를 JSON 형태로 응답
        String jsonResponse = """
            {
                "error": "Unauthorized",
                "message": "JWT 토큰이 필요합니다. Authorization 헤더에 'Bearer <token>' 형식으로 토큰을 포함해주세요.",
                "status": 401,
                "path": "%s"
            }
            """.formatted(request.getRequestURI());
        
        // 4. 응답 본문에 에러 메시지 작성
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}