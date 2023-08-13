package com.example.wpob.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 로그인, 회원가입 Input
 */
@Getter
@Setter
@ToString
@Builder
public class UserSignDto {

    @Email(message = "잘못된 이메일 형식입니다.")
    private String email;

    @JsonInclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8, message = "비밀번호는 8자 이상 이어야 합니다.")
    private String password;

}
