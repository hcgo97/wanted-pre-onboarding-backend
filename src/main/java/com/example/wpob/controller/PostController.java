package com.example.wpob.controller;

import com.example.wpob.dto.ApiResponse;
import com.example.wpob.dto.post.PostInfoDto;
import com.example.wpob.dto.post.PostEditDto;
import com.example.wpob.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
