package com.example.new_back_end.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Spring Security 필터 체인 설정
     * - JWT 기반 인증을 위한 설정
     * - JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 배치
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())             // CSRF 비활성화 (JWT 사용으로 불필요)
                .formLogin(form -> form.disable())        // 폼 로그인 비활성화 (JWT 사용)
                .httpBasic(basic -> basic.disable())      // HTTP Basic 인증 비활성화 (JWT 사용)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 세션 비활성화 (JWT로 상태관리)
                
                // JWT 필터를 Spring Security 필터 체인에 추가
                // UsernamePasswordAuthenticationFilter 이전에 실행되도록 설정
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()           // 인증 API는 토큰 없이 접근 가능
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()  // Swagger 문서 접근 허용
                        .requestMatchers("/h2-console/**").permitAll()         // H2 Console 접근 허용 (개발용)
                        .anyRequest().authenticated()                          // 나머지 모든 요청은 JWT 토큰 필요
                )
                
                // H2 Console 사용을 위한 헤더 설정 (개발 환경에서만 필요)
                .headers(headers -> headers.frameOptions().disable());
        
        return http.build();
    }
}