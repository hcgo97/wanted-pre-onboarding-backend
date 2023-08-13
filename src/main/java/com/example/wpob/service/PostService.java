package com.example.wpob.service;

import com.example.wpob.dto.post.PostEditDto;
import com.example.wpob.dto.post.PostInfoDto;
import com.example.wpob.entity.Posts;
import com.example.wpob.entity.Users;
import com.example.wpob.repository.PostsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostsRepository postsRepository;

    /**
     * 게시글 작성
     */
    @Transactional(rollbackOn = Exception.class)
    public PostInfoDto createPost(PostEditDto inputDto, Users users) {

        // 1. 게시글 생성 및 저장
        Posts posts = Posts.create(users, inputDto.getTitle(), inputDto.getContents());
        postsRepository.save(posts);

        // 2. 게시글 정보 객체 return
        return PostInfoDto.builder()
                .id(posts.getId())
                .author(users.getEmail())
                .createdAt(posts.getCreatedAt())
                .updatedAt(posts.getUpdatedAt())
                .isUpdated(false)
                .title(posts.getTitle())
                .contents(posts.getContents())
                .build();
    }

}
