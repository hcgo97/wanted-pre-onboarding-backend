package com.example.wpob.dto.user;

import com.example.wpob.entity.Users;
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

    // Users -> UserInfoDto
    public static UserInfoDto convertUser(Users users) {
        return UserInfoDto.builder()
                .id(users.getId())
                .email(users.getEmail())
                .build();
    }

}
