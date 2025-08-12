package com.example.new_back_end.controller;

import com.example.new_back_end.dto.LoginRequestDTO;
import com.example.new_back_end.dto.LoginResponseDTO;
import com.example.new_back_end.dto.SignUpRequestDTO;
import com.example.new_back_end.dto.SignUpResponseDTO;
import com.example.new_back_end.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "인증 API", description = "회원가입, 로그인 관련 API")
public class AuthController {
    private final UserService userService;

    @PostMapping("/signup")
    @Operation(
            summary = "회원가입",
            description = "새로운 사용자 계정을 생성합니다. 사용자명, 이메일, 비밀번호가 필요합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "중복된 사용자명 또는 이메일"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> signUp(
            @Parameter(description = "회원가입 정보 (사용자명, 이메일, 비밀번호)")
            @Valid @RequestBody SignUpRequestDTO signUpRequestDTO
    ) {
        try {
            SignUpResponseDTO response = userService.signUp(signUpRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            // 중복된 사용자명 / 이메일인 경우
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // 기타 예외 상황
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 중 오류가 발생했습니다");
        }
    }

    @PostMapping("/login")
    @Operation(
            summary = "로그인",
            description = "사용자 인증을 수행하고 JWT 토큰을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공, JWT 토큰 반환"),
            @ApiResponse(responseCode = "400", description = "잘못된 사용자명 또는 비밀번호"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> login(
            @Parameter(description = "로그인 정보 (사용자명, 비밀번호)")
            @Valid @RequestBody LoginRequestDTO loginRequestDTO
    ) {
        try {
            LoginResponseDTO response = userService.login(loginRequestDTO);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // 사용자명이 존재하지 않거나 비밀번호가 일치하지 않는 경우
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // 기타 예외 상황
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그인 중 오류가 발생했습니다");
        }
    }
}
