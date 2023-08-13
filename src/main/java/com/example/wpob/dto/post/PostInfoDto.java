package com.example.wpob.dto.post;

import com.example.wpob.entity.Posts;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 게시글 정보
 */
@Getter
@Setter
@ToString
@Builder
public class PostInfoDto {

    private Long id;

    private String author;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime updatedAt;

    private Boolean isUpdated = false;

    private String title;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String contents;

    // Posts -> PostInfoDto (상세 조회용)
    public static PostInfoDto convertPostDetail(Posts posts) {
        return PostInfoDto.builder()
                .id(posts.getId())
                .author(posts.getUsers().getEmail())
                .createdAt(posts.getCreatedAt())
                .updatedAt(posts.getUpdatedAt())
                .isUpdated(posts.isUpdated())
                .title(posts.getTitle())
                .contents(posts.getContents())
                .build();
    }

    // Posts -> PostInfoDto (목록 조회용)
    public static PostInfoDto convertPostList(Posts posts) {
        return PostInfoDto.builder()
                .id(posts.getId())
                .author(posts.getUsers().getEmail())
                .createdAt(posts.getCreatedAt())
                .isUpdated(posts.isUpdated())
                .title(posts.getTitle())
                .build();
    }

}
