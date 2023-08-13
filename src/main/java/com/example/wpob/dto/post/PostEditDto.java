package com.example.wpob.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 게시글 작성, 수정 Input
 */
@Getter
@Setter
@ToString
@Builder
public class PostEditDto {

    @Size(min = 1, max = 10, message = "제목은 10자 이하로 입력하여야 합니다.")
    @NotBlank
    private String title;

    @Size(min = 1, max = 300, message = "내용은 300자 이하로 입력하여야 합니다.")
    @NotBlank
    private String contents;

}
