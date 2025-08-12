package com.example.new_back_end.repository;

import com.example.new_back_end.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 사용자명으로 사용자 찾기 ( 로그인 시 사용)
    Optional<User> findByUsername(String username);

    // 이메일로 사용자 찾기 ( 중복 체크용)
    Optional<User> findByEmail(String email);

    // 사용자명 중복 체크
    boolean existsByUsername(String username);

    // 이메일 중복 체크
    boolean existsByEmail(String email);
}
