package com.example.new_back_end.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponseDTO {

    private String token;           // JWT 토큰
    private String username;        // 로그인한 사용자명
    private String message;         // 응답 메시지
}
