package com.example.new_back_end.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())             // CSRF 비활성화
                .formLogin(form -> form.disable())   // 폼 로그인 비활성화
                .httpBasic(basic -> basic.disable())  // HTTP Basic 인증 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()           // 인증 API는 모든 사용자 허용
                        .anyRequest().authenticated()                            // 나머지는 인증 필요
                );
        return http.build();
    }
}