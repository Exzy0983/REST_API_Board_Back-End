package com.example.new_back_end.service;

import com.example.new_back_end.dto.RequestDTO;
import com.example.new_back_end.dto.ResponseDTO;
import com.example.new_back_end.entity.Post;
import com.example.new_back_end.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    // 게시글 생성
    public ResponseDTO create(RequestDTO requestDTO) {
        Post post = toEntity(requestDTO);
        Post createPost = postRepository.save(post);
        return toDTO(createPost);
    }

    // 게시글 조회(단일)
    public ResponseDTO read(Long id) {
        Post readPost = postRepository.findById(id).orElse(null);
        if (readPost == null) {
            return null;
        }
        return toDTO(readPost);
    }

    // 게시글 조회(전체)
    public List<ResponseDTO> readAll() {
        List<Post> posts = postRepository.findAll();
        List<ResponseDTO> result = new ArrayList<ResponseDTO>();
        for (Post post : posts) {
            ResponseDTO dto = toDTO(post);
            result.add(dto);
        }
        return result;
    }

    // 게시글 수정
    public ResponseDTO update(Long id, RequestDTO requestDTO) {
        Post post = postRepository.findById(id).orElse(null);

        post.setTitle(requestDTO.getTitle());
        post.setContent(requestDTO.getContent());
        post.setAuthor(requestDTO.getAuthor());

        Post updatedPost = postRepository.save(post);
        return toDTO(updatedPost);
    }
    // 게시글 삭제

    public void delete(Long id) {
        postRepository.deleteById(id);
    }

    // entity -> dto
    public ResponseDTO toDTO(Post post) {
        ResponseDTO dto = new ResponseDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setAuthor(post.getAuthor());
        dto.setCreatedDate(post.getCreatedDate());
        dto.setUpdatedDate(post.getUpdatedDate());
        dto.setViewCount(post.getViewCount());
        return dto;
    }

    // dto -> entity
    public Post toEntity(RequestDTO dto) {
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setAuthor(dto.getAuthor());
        return post;
    }
}
