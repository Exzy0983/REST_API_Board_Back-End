package com.example.new_back_end.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 전역 예외 처리 핸들러
 * - JWT 관련 예외들을 처리
 * - 일관된 에러 응답 형식 제공
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * JWT 토큰 만료 예외 처리
     * @param e 만료된 JWT 예외
     * @return 401 응답과 에러 메시지
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Map<String, Object>> handleExpiredJwtException(ExpiredJwtException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Token Expired");
        errorResponse.put("message", "JWT 토큰이 만료되었습니다. 다시 로그인해주세요.");
        errorResponse.put("status", 401);
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * 일반적인 JWT 예외 처리 (잘못된 토큰, 서명 오류 등)
     * @param e JWT 예외
     * @return 401 응답과 에러 메시지
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, Object>> handleJwtException(JwtException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Invalid Token");
        errorResponse.put("message", "유효하지 않은 JWT 토큰입니다: " + e.getMessage());
        errorResponse.put("status", 401);
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * 일반적인 예외 처리
     * @param e 일반 예외
     * @return 500 응답과 에러 메시지
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", "서버 내부 오류가 발생했습니다.");
        errorResponse.put("status", 500);
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}