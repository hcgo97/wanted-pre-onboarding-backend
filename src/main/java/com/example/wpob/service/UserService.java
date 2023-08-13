package com.example.wpob.service;

import com.example.wpob.dto.UserInfoDto;
import com.example.wpob.dto.UserSignDto;
import com.example.wpob.entity.Users;
import com.example.wpob.exception.ApiResultStatus;
import com.example.wpob.exception.UserException;
import com.example.wpob.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     */
    @Transactional(rollbackOn = Exception.class)
    public UserInfoDto join(UserSignDto inputDto) {
        // 1. 이메일 유효성 검사
        if (usersRepository.findByEmailAndIsDeletedIsFalse(inputDto.getEmail()).isPresent()) {
            throw new UserException(ApiResultStatus.ALREADY_SIGNED_UP);
        }

        // 2. 비밀번호 암호화
        inputDto.setPassword(passwordEncoder.encode(inputDto.getPassword()));

        // 3. 유저 생성
        Users users = Users.create(inputDto.getEmail(), inputDto.getPassword());
        usersRepository.save(users);

        // 4. 유저 정보 객체 return
        return UserInfoDto.builder()
                .id(users.getId())
                .email(users.getEmail())
                .build();
    }

}
