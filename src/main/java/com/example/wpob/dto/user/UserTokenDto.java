package com.example.wpob.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 유저 로그인 Output
 */
@Getter
@Setter
@ToString
public class UserTokenDto {

    private String accessToken;
    private UserInfoDto userInfo;

    @Builder
    public UserTokenDto(String accessToken, Long userId, String email) {
        this.accessToken = accessToken;
        this.userInfo = UserInfoDto.builder()
                .id(userId)
                .email(email)
                .build();
    }

}
