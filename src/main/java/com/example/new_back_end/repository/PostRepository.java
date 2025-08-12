package com.example.new_back_end.repository;

import com.example.new_back_end.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
