# JWT 인증 시스템 완전 구현 완료

## 📋 개요
기존의 최소한의 JWT 기능에서 완전한 JWT 인증 시스템으로 확장 구현하였습니다.

## 🚀 추가된 기능들

### 1. **JwtAuthenticationFilter** (새로 추가)
**위치**: `src/main/java/com/example/new_back_end/config/JwtAuthenticationFilter.java`

**주요 기능**:
- HTTP 요청을 가로채서 JWT 토큰 검증
- Authorization 헤더에서 "Bearer " 토큰 추출
- 유효한 토큰 시 SecurityContext에 인증 정보 설정
- OncePerRequestFilter 상속으로 요청당 1회만 실행

**핵심 동작 과정**:
```java
// 1. HTTP 헤더에서 JWT 토큰 추출
String token = jwtUtil.extractTokenFromRequest(request);

// 2. 토큰 존재 & 인증 정보 없음 확인
if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
    
    // 3. 토큰에서 사용자명 추출
    String username = jwtUtil.getUsernameFromToken(token);
    
    // 4. 토큰 유효성 검증
    if (jwtUtil.validateToken(token, username)) {
        
        // 5. 인증 토큰 생성 및 SecurityContext 설정
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            username, null, new ArrayList<>()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
```

### 2. **JwtUtil 확장** (기존 파일 수정)
**위치**: `src/main/java/com/example/new_back_end/util/JwtUtil.java`

**추가된 메서드**:

#### `extractTokenFromRequest(HttpServletRequest request)`
- HTTP 요청에서 JWT 토큰 추출
- "Authorization: Bearer <token>" 형식 처리
- Bearer 접두사 제거 후 순수 토큰만 반환

```java
public String extractTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
        return bearerToken.substring(7);  // "Bearer " 제거
    }
    return null;
}
```

#### `getExpirationFromToken(String token)`
- JWT 토큰의 만료 시간 조회
- 토큰 상태 확인 및 디버깅용

**이제 사용되는 기존 메서드**:
- `validateToken(String token, String username)` - 필터에서 호출됨
- `getUsernameFromToken(String token)` - 필터에서 사용자명 추출용

### 3. **SecurityConfig 업데이트** (기존 파일 수정)
**위치**: `src/main/java/com/example/new_back_end/config/SecurityConfig.java`

**주요 변경사항**:

#### JWT 필터 체인 등록
```java
.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
```
- JWT 필터를 Spring Security 필터 체인에 추가
- UsernamePasswordAuthenticationFilter 이전에 실행되도록 설정

#### 예외 처리 진입점 설정
```java
.exceptionHandling(exceptions -> exceptions
    .authenticationEntryPoint(jwtAuthenticationEntryPoint))
```

#### 접근 권한 세분화
```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/auth/**").permitAll()           // 로그인/회원가입
    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()  // API 문서
    .requestMatchers("/h2-console/**").permitAll()         // 개발용 DB
    .anyRequest().authenticated()                          // 나머지는 JWT 토큰 필요
)
```

### 4. **JWT 예외 처리 시스템** (새로 추가)

#### JwtAuthenticationEntryPoint
**위치**: `src/main/java/com/example/new_back_end/exception/JwtAuthenticationEntryPoint.java`

**기능**:
- 인증되지 않은 사용자 접근 시 호출
- 401 Unauthorized 응답 반환
- 친화적인 JSON 에러 메시지 제공

```json
{
    "error": "Unauthorized",
    "message": "JWT 토큰이 필요합니다. Authorization 헤더에 'Bearer <token>' 형식으로 토큰을 포함해주세요.",
    "status": 401,
    "path": "/api/posts"
}
```

#### GlobalExceptionHandler
**위치**: `src/main/java/com/example/new_back_end/exception/GlobalExceptionHandler.java`

**처리하는 예외들**:
- `ExpiredJwtException`: 토큰 만료
- `JwtException`: 잘못된 토큰, 서명 오류 등
- `Exception`: 일반적인 서버 오류

## 🔄 전체 JWT 인증 흐름

### 1. 로그인 과정
1. **POST /api/auth/login** → 사용자명/비밀번호 검증
2. **JWT 토큰 생성** → JwtUtil.generateToken() 호출
3. **토큰 반환** → 클라이언트가 토큰 저장

### 2. 보호된 API 접근 과정
1. **HTTP 요청** → Authorization: Bearer <token>
2. **JwtAuthenticationFilter 실행**:
   - 토큰 추출 (extractTokenFromRequest)
   - 사용자명 추출 (getUsernameFromToken) 
   - 토큰 검증 (validateToken) ✅ **이제 사용됨!**
   - SecurityContext 설정
3. **Controller 실행** → 인증된 사용자로 처리
4. **응답 반환**

### 3. 인증 실패 시
- **토큰 없음/잘못됨** → JwtAuthenticationEntryPoint → 401 응답
- **토큰 만료** → GlobalExceptionHandler → 401 응답 (재로그인 안내)

## 📡 API 사용 방법

### 로그인 후 토큰 획득
```bash
POST /api/auth/login
{
    "username": "testuser",
    "password": "password123"
}

# 응답
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "username": "testuser"
}
```

### 보호된 API 호출
```bash
GET /api/posts
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

POST /api/posts
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
{
    "title": "새 게시글",
    "content": "내용",
    "author": "작성자"
}
```

## ⚙️ 설정값 (application.yml/properties)

JWT 관련 설정이 필요합니다:
```yaml
jwt:
  secret: mySecretKey123!@#  # 실제로는 더 복잡한 키 사용
  expiration: 86400000       # 24시간 (밀리초)
```

## 🔒 보안 특징

1. **무상태(Stateless)**: 세션 사용하지 않음
2. **토큰 기반**: JWT 토큰으로 인증 상태 관리
3. **요청별 검증**: 모든 요청마다 토큰 유효성 검증
4. **예외 처리**: 다양한 JWT 오류 상황별 적절한 응답
5. **CORS 준비**: RESTful API로 프론트엔드와 분리 가능

## ✅ 테스트 시나리오

1. **토큰 없이 보호된 API 호출** → 401 에러
2. **잘못된 토큰으로 API 호출** → 401 에러  
3. **만료된 토큰으로 API 호출** → 401 에러 (재로그인 안내)
4. **유효한 토큰으로 API 호출** → 정상 처리
5. **로그인/회원가입 API** → 토큰 없이도 접근 가능

이제 `JwtUtil.validateToken()` 메서드가 실제로 사용되며, 완전한 JWT 인증 시스템이 구축되었습니다! 🎉