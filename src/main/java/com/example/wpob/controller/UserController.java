package com.example.wpob.controller;

import com.example.wpob.dto.*;
import com.example.wpob.dto.user.UserInfoDto;
import com.example.wpob.dto.user.UserSignDto;
import com.example.wpob.dto.user.UserTokenDto;
import com.example.wpob.service.UserService;
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
@RequestMapping("${api.path.default}/users")
public class UserController extends BaseController {

    private final UserService userService;

    /**
     * 회원가입
     */
    @PostMapping("/join")
    public ResponseEntity<ApiResponse> join(@Validated @RequestBody UserSignDto inputDto) {
        UserInfoDto resultDto = userService.join(inputDto);
        return responseBuilder(resultDto, HttpStatus.CREATED);
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Validated @RequestBody UserSignDto inputDto) {
        UserTokenDto resultDto = userService.login(inputDto);
        return responseBuilder(resultDto, HttpStatus.OK);
    }

}
