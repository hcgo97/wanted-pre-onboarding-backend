package com.example.wpob.service;

import com.example.wpob.dto.post.PostEditDto;
import com.example.wpob.dto.post.PostInfoDto;
import com.example.wpob.entity.Posts;
import com.example.wpob.entity.Users;
import com.example.wpob.exception.ApiResultStatus;
import com.example.wpob.exception.PostException;
import com.example.wpob.repository.PostsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostsRepository postsRepository;

    /**
     * 게시글 작성
     */
    @Transactional
    public PostInfoDto createPost(PostEditDto inputDto, Users users) {
        // 1. 게시글 생성 및 저장
        Posts posts = Posts.create(users, inputDto.getTitle(), inputDto.getContents());
        postsRepository.save(posts);

        // 2. 게시글 정보 객체 return
        return PostInfoDto.convertPostDetail(posts);
    }

    /**
     * 게시글 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<PostInfoDto> showPostList(Pageable pageable) {
        // 1. 삭제되지 않은 게시글 목록 조회
        Page<Posts> posts = postsRepository.findByIsDeletedIsFalseOrderByCreatedAtDesc(pageable);

        // 2. 게시글 목록 객체 return
        return posts.map(PostInfoDto::convertPostList);
    }

    /**
     * 게시글 상세 조회
     */
    @Transactional(readOnly = true)
    public PostInfoDto showPostDetail(Long postId) {
        // 1. 삭제되지 않은 게시글인지 조회
        Posts posts = getPost(postId);

        // 2. 게시글 정보 객체 return
        return PostInfoDto.convertPostDetail(posts);
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public PostInfoDto editPost(Long postId, PostEditDto inputDto, Users users) {
        // 1. 삭제되지 않은 게시글인지 조회
        Posts posts = getPost(postId);

        // 2. 게시글 권한 확인
        checkPostAuth(posts, users);

        // 3. 게시글 수정 및 저장
        posts.update(inputDto.getTitle(), inputDto.getContents());
        postsRepository.save(posts);

        // 4. 게시글 정보 객체 return
        return PostInfoDto.convertPostDetail(posts);
    }

    private Posts getPost(Long postId) {
        return postsRepository.findByIdAndIsDeletedIsFalse(postId)
                .orElseThrow(() -> new PostException(ApiResultStatus.POST_NOT_FOUND));
    }

    private void checkPostAuth(Posts posts, Users users) {
        if (!posts.getUsers().getId().equals(users.getId())) {
            throw new PostException(ApiResultStatus.NOT_MY_POST);
        }
    }

}
