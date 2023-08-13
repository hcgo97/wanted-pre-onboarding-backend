package com.example.wpob.controller;

import com.example.wpob.dto.ApiResponse;
import com.example.wpob.dto.UserInfoDto;
import com.example.wpob.dto.UserSignDto;
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

}
