package com.example.wpob.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 유저 정보
 */
@Getter
@Setter
@ToString
@Builder
public class UserInfoDto {

    private Long id;
    private String email;

}
