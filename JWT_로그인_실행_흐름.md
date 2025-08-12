# JWT 기반 로그인 기능 실행 흐름

## 📋 개요
클라이언트가 로그인 요청을 보낼 때부터 JWT 토큰을 받기까지의 전체 실행 흐름을 단계별로 정리했습니다.

## 🔄 로그인 요청 처리 흐름

### 1단계: 클라이언트 요청
```http
POST /api/auth/login
Content-Type: application/json

{
    "username": "testuser",
    "password": "password123"
}
```

### 2단계: Spring Security 필터 체인 통과
**위치**: Spring Security 내부
- 🔓 `/api/auth/**` 경로는 `.permitAll()`로 설정되어 있어서 JWT 토큰 없이도 접근 가능
- `JwtAuthenticationFilter`도 실행되지만 Authorization 헤더가 없으므로 그냥 통과

### 3단계: AuthController.login() 메서드 호출
**위치**: `AuthController.java:65`
```java
public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
    try {
        // 4단계로 넘어감
        LoginResponseDTO response = userService.login(loginRequestDTO);
        return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
```

**실행되는 코드**:
- `@Valid` 애너테이션으로 LoginRequestDTO 유효성 검증
- `userService.login()` 호출

### 4단계: UserService.login() 메서드 호출
**위치**: `UserService.java` (해당 메서드)
```java
public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
    // 4-1. 사용자 존재 여부 확인
    User user = userRepository.findByUsername(loginRequestDTO.getUsername())
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
    
    // 4-2. 비밀번호 검증
    if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
        throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
    }
    
    // 4-3. JWT 토큰 생성 (5단계로 넘어감)
    String token = jwtUtil.generateToken(user.getUsername());
    
    // 4-4. 응답 DTO 생성 및 반환
    return new LoginResponseDTO(token, user.getUsername());
}
```

**실행 순서**:
1. `userRepository.findByUsername()` 호출 → **5-1단계**
2. `passwordEncoder.matches()` 호출 → **5-2단계**  
3. `jwtUtil.generateToken()` 호출 → **5-3단계**
4. `LoginResponseDTO` 생성 후 반환

### 5단계: 하위 메서드들 실행

#### 5-1단계: UserRepository.findByUsername() 
**위치**: `UserRepository.java`
```java
Optional<User> findByUsername(String username);
```
- **JPA가 자동 생성한 메서드**
- 데이터베이스에서 `SELECT * FROM users WHERE username = ?` 쿼리 실행
- `Optional<User>` 반환 (사용자 존재하면 User 객체, 없으면 empty)

#### 5-2단계: PasswordEncoder.matches()
**위치**: Spring Security 내부 (`BCryptPasswordEncoder`)
```java
boolean matches(CharSequence rawPassword, String encodedPassword)
```
- 평문 비밀번호(`loginRequestDTO.getPassword()`)와 암호화된 비밀번호(`user.getPassword()`) 비교
- BCrypt 해싱 알고리즘으로 검증
- `true`/`false` 반환

#### 5-3단계: JwtUtil.generateToken()
**위치**: `JwtUtil.java:21`
```java
public String generateToken(String username) {
    return Jwts.builder()
            .setSubject(username)                    // 토큰 주체 (사용자명)
            .setIssuedAt(new Date())                // 토큰 발급 시간
            .setExpiration(new Date(System.currentTimeMillis() + expiration))  // 토큰 만료 시간
            .signWith(SignatureAlgorithm.HS256, secretKey)  // 서명 알고리즘과 비밀키
            .compact();                             // 토큰 생성
}
```
- JWT 라이브러리(`jjwt`)를 사용하여 토큰 생성
- 사용자명을 subject로 설정
- 현재 시간을 발급 시간으로 설정
- 설정된 만료 시간(application.yml의 `jwt.expiration`) 추가
- HMAC SHA256으로 서명
- 최종 JWT 토큰 문자열 반환

### 6단계: 응답 반환
**위치**: `AuthController.java:70`
```java
LoginResponseDTO response = userService.login(loginRequestDTO);
return ResponseEntity.ok(response);
```

**응답 데이터**:
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTY5...",
    "username": "testuser"
}
```

## 🏗️ 전체 실행 흐름 요약

```
클라이언트 요청
    ↓
Spring Security 필터 체인 (permit 통과)
    ↓
AuthController.login()
    ↓
UserService.login()
    ├─ UserRepository.findByUsername() → DB 조회
    ├─ PasswordEncoder.matches() → 비밀번호 검증
    └─ JwtUtil.generateToken() → JWT 토큰 생성
    ↓
LoginResponseDTO 생성
    ↓
ResponseEntity.ok() 응답
    ↓
클라이언트에게 JWT 토큰 전달
```

## 📁 관련 파일들

| 단계 | 파일 위치 | 클래스/메서드 |
|------|-----------|---------------|
| 3 | `controller/AuthController.java` | `login()` |
| 4 | `service/UserService.java` | `login()` |
| 5-1 | `repository/UserRepository.java` | `findByUsername()` |
| 5-2 | Spring Security 내부 | `PasswordEncoder.matches()` |
| 5-3 | `util/JwtUtil.java` | `generateToken()` |

## 🔐 보안 처리 과정

1. **입력 검증**: `@Valid`로 LoginRequestDTO 유효성 확인
2. **사용자 존재 확인**: DB에서 username으로 사용자 조회
3. **비밀번호 검증**: BCrypt로 평문-암호문 비교
4. **토큰 생성**: 검증된 사용자명으로 JWT 토큰 생성
5. **안전한 응답**: 비밀번호는 응답에 포함하지 않음

## ⚡ 성능 고려사항

- **DB 조회**: username에 인덱스 설정 권장
- **비밀번호 검증**: BCrypt는 의도적으로 느린 알고리즘 (보안 강화)
- **JWT 생성**: 메모리 내에서 빠른 처리
- **예외 처리**: 빠른 실패로 불필요한 연산 방지

이제 클라이언트가 이 JWT 토큰을 받아서 다른 API 호출 시 `Authorization: Bearer <token>` 헤더에 포함하면 `JwtAuthenticationFilter`가 이를 검증하게 됩니다! 🎉