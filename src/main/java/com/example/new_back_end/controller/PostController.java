package com.example.new_back_end.controller;

import com.example.new_back_end.dto.RequestDTO;
import com.example.new_back_end.dto.ResponseDTO;
import com.example.new_back_end.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "게시판 API", description = "게시글 CRUD 관련 API")
public class PostController {

    private final PostService postService;

    // 게시글 생성
    @PostMapping("/api/posts")
    @Operation(
            summary = "게시글 생성",
            description = "새로운 게시글을 생성합니다. 제목, 내용, 작성자 정보가 필요합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    public ResponseDTO create(
            @Parameter(description = "게시글 생성 정보 (제목, 내용, 작성자)")
            @RequestBody RequestDTO requestDTO
    ) {
        return postService.create(requestDTO);
    }

    // 게시글 조회(단일)
    @GetMapping("/api/posts/{id}")
    @Operation(
            summary = "특정 게시글 조회",
            description = "게시글 ID를 통해 특정 게시글의 상세 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    public ResponseDTO read(
            @Parameter(description = "조회할 게시글의 ID", example = "1")
            @PathVariable Long id
    ) {
        return postService.read(id);
    }

    // 게시글 조회(전체)
    @GetMapping("/api/posts")
    @Operation(
            summary = "전체 게시글 조회",
            description = "등록된 모든 게시글의 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공"),
            @ApiResponse(responseCode = "204", description = "등록된 게시글이 없음")
    })
    public List<ResponseDTO> readAll() {
        return postService.readAll();
    }

    // 게시글 수정
    @PutMapping("/api/posts/{id}")
    @Operation(
            summary = "게시글 수정",
            description = "기존 게시글의 내용을 수정합니다. 제목, 내용, 작성자를 변경할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @ApiResponse(responseCode = "404", description = "수정할 게시글을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    public ResponseDTO update(
            @Parameter(description = "수정할 게시글의 ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "수정할 게시글 정보 (제목, 내용, 작성자)")
            @RequestBody RequestDTO requestDTO
    ){
        return postService.update(id, requestDTO);
    }

    // 게시글 삭제
    @DeleteMapping("/api/posts/{id}")
    @Operation(
            summary = "게시글 삭제",
            description = "특정 게시글을 삭제합니다. 삭제된 게시글은 복구할 수 없습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "삭제할 게시글을 찾을 수 없음")
    })
    public void delete(
            @Parameter(description = "삭제할 게시글의 ID", example = "1")
            @PathVariable Long id
    ) {
        postService.delete(id);
    }
}