package com.example.wpob.controller;

import com.example.wpob.dto.ApiResponse;
import com.example.wpob.dto.post.PostEditDto;
import com.example.wpob.dto.post.PostInfoDto;
import com.example.wpob.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.path.default}/posts")
public class PostController extends BaseController {

    private final PostService postService;

    /**
     * 게시글 작성
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse> createPost(@Validated @RequestBody PostEditDto inputDto) {
        PostInfoDto resultDto = postService.createPost(inputDto, getUsers());
        return responseBuilder(resultDto, HttpStatus.CREATED);
    }

    /**
     * 게시글 목록 조회
     */
    @GetMapping("")
    public ResponseEntity<ApiResponse> showPostList(Pageable pageable) {
        Page<PostInfoDto> resultDto = postService.showPostList(pageable);
        return responseBuilder(resultDto, HttpStatus.OK);
    }

    /**
     * 게시글 상세 조회
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse> showPostDetail(@PathVariable Long postId) {
        PostInfoDto resultDto = postService.showPostDetail(postId);
        return responseBuilder(resultDto, HttpStatus.OK);
    }

}
