package com.example.new_back_end.service;

import com.example.new_back_end.dto.SignUpRequestDTO;
import com.example.new_back_end.dto.SignUpResponseDTO;
import com.example.new_back_end.entity.User;
import com.example.new_back_end.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignUpResponseDTO signUp(SignUpRequestDTO signUpRequestDTO) {
        // 1. 중복 체크
        if( userRepository.existsByUsername(signUpRequestDTO.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 사용자 명입니다.");
        }

        if(userRepository.existsByEmail(signUpRequestDTO.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일 입니다.");
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signUpRequestDTO.getPassword());

        // 3. User Entity 생성
        User user = new User();
        user.setUsername(signUpRequestDTO.getUsername());
        user.setEmail(signUpRequestDTO.getEmail());
        user.setPassword(encodedPassword);
        user.setRole(User.Role.USER);

        // 4. 데이터베이스에 저장
        User savedUser = userRepository.save(user);

        SignUpResponseDTO response = new SignUpResponseDTO();
        response.setId(savedUser.getId());
        response.setUsername(savedUser.getUsername());
        response.setEmail(savedUser.getEmail());
        response.setMessage("회원가입이 성공적으로 완료되었습니다");

        return response;
    }
}
