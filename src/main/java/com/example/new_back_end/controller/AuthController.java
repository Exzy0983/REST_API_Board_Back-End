package com.example.new_back_end.controller;

import com.example.new_back_end.dto.SignUpRequestDTO;
import com.example.new_back_end.dto.SignUpResponseDTO;
import com.example.new_back_end.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequestDTO signUpRequestDTO) {
        try {
            SignUpResponseDTO response = userService.signUp(signUpRequestDTO);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // 중복된 사용자명 / 이메일인 경우
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // 기타 예외 상황
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 중 오류가 발생햇습니다");
        }
    }
}
