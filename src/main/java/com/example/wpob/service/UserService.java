package com.example.wpob.service;

import com.example.wpob.dto.user.UserInfoDto;
import com.example.wpob.dto.user.UserSignDto;
import com.example.wpob.dto.user.UserTokenDto;
import com.example.wpob.entity.Users;
import com.example.wpob.exception.ApiResultStatus;
import com.example.wpob.exception.UserException;
import com.example.wpob.repository.UsersRepository;
import com.example.wpob.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입
     */
    @Transactional
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
        return UserInfoDto.convertUser(users);
    }

    /**
     * 로그인
     */
    @Transactional(readOnly = true)
    public UserTokenDto login(UserSignDto inputDto) {
        // 1. 로그인 정보 확인
        Users users = usersRepository.findByEmailAndIsDeletedIsFalse(inputDto.getEmail())
                .filter(user -> passwordEncoder.matches(inputDto.getPassword(), user.getPassword()))
                .orElseThrow(() -> new UserException(ApiResultStatus.LOGIN_FAILED)); // 비밀번호 틀렸으면 로그인 실패

        // 2. 토큰 생성
        String accessToken = jwtUtil.createToken(users.getId(), users.getEmail());

        // 3. 토큰 & 유저 정보 객체 return
        return UserTokenDto.builder()
                .accessToken(accessToken)
                .users(users)
                .build();
    }

}
