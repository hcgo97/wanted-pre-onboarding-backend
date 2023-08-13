package com.example.wpob.service;

import com.example.wpob.dto.user.UserSignDto;
import com.example.wpob.dto.user.UserTokenDto;
import com.example.wpob.exception.ApiResultStatus;
import com.example.wpob.exception.UserException;
import com.example.wpob.repository.UsersRepository;
import com.example.wpob.util.JwtUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    JwtUtil jwtUtil;

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    public static void init() {
        // 유효성 검사 객체 초기화
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    public static void close() {
        factory.close();
    }

    @BeforeEach
    void beforeJoin() {
        // 테스트 전에 회원가입 하기
        UserSignDto userSignDto = UserSignDto.builder()
                .email("test1@abc.com")
                .password("test1234")
                .build();

        userService.join(userSignDto);
    }

    @Nested
    @DisplayName("파라미터 유효성 검증 테스트")
    @Transactional
    class validation_test {

        @Test
        @DisplayName("잘못된 이메일 형식")
        void email_check() {
            UserSignDto userSignDto = UserSignDto.builder()
                    .email("abc1234.com")
                    .password("test1234")
                    .build();

            // 이메일 형식인지 체크
            Set<ConstraintViolation<UserSignDto>> violations = validator.validate(userSignDto);
            violations.forEach(error -> {
                assertThat(error.getMessage()).isEqualTo("잘못된 이메일 형식입니다.");
            });
        }

        @Test
        @DisplayName("잘못된 비밀번호 형식")
        void password_check() {
            UserSignDto userSignDto = UserSignDto.builder()
                    .email("abc@1234.com")
                    .password("1234567")
                    .build();

            // 비밀번호가 8자리 이상인지 체크
            Set<ConstraintViolation<UserSignDto>> violations = validator.validate(userSignDto);
            violations.forEach(error -> {
                assertThat(error.getMessage()).isEqualTo("비밀번호는 8자 이상 이어야 합니다.");
            });
        }
    }


    @Nested
    @DisplayName("회원가입 테스트")
    @Transactional
    class join_test {

        @Test
        @DisplayName("S01 - 회원가입 성공")
        void join_success_case1() {
            UserSignDto userSignDto = UserSignDto.builder()
                    .email("test1@abc.com")
                    .password("test1234")
                    .build();

            // 정상 가입 되었는지 조회
            assertTrue(usersRepository.findByEmailAndIsDeletedIsFalse(userSignDto.getEmail()).isPresent());
        }

        @Test
        @DisplayName("E01 - 이미 가입된 이메일")
        void join_failed_case1() {
            UserSignDto userSignDto = UserSignDto.builder()
                    .email("test1@abc.com")
                    .password("test1234")
                    .build();

            // 중복가입 되지 않는지 확인
            assertThrows(UserException.class, () -> {
                userService.join(userSignDto);
            }, ApiResultStatus.ALREADY_SIGNED_UP.getMessage());
        }
    }


    @Nested
    @DisplayName("로그인 테스트")
    @Transactional
    class login_test {

        @Test
        @DisplayName("S01 - 로그인 성공")
        void login_success_case1() {
            UserSignDto userSignDto = UserSignDto.builder()
                    .email("test1@abc.com")
                    .password("test1234")
                    .build();

            assertDoesNotThrow(() -> {
                // 로그인 시도
                UserTokenDto userTokenDto = userService.login(userSignDto);
                // token 정상 발급되었는지 조회
                assertNotNull(userTokenDto.getAccessToken());
                // token 유효성 검사
                assertTrue(jwtUtil.validateJwtToken(userTokenDto.getAccessToken()));
            });
        }

        @Test
        @DisplayName("E01 - 로그인 정보가 일치하지 않음")
        void login_failed_case1() {
            UserSignDto userSignDto = UserSignDto.builder()
                    .email("test1@abc.com")
                    .password("invalid_password")
                    .build();

            // 잘못된 비밀번호 테스트
            assertThrows(UserException.class, () -> {
                userService.login(userSignDto);
            }, ApiResultStatus.LOGIN_FAILED.getMessage());
        }
    }

}