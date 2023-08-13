package com.example.wpob.service;

import com.example.wpob.dto.UserSignDto;
import com.example.wpob.exception.ApiResultStatus;
import com.example.wpob.exception.UserException;
import com.example.wpob.repository.UsersRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles(value = "local")
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UsersRepository usersRepository;

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


    @Nested
    @DisplayName("회원가입 테스트")
    @Transactional
    class join_test {
        // 회원가입 DTO
        UserSignDto userSignDto = UserSignDto.builder()
                .email("test1@abc.com")
                .password("test1234")
                .build();

        @BeforeEach
        void setUp() {
            // 회원가입 하기
            userService.join(userSignDto);
        }

        @Test
        @DisplayName("S01 - 회원가입 성공")
        void join_success_case1() {
            // 정상 가입 되었는지 조회
            assertTrue(usersRepository.findByEmailAndIsDeletedIsFalse(userSignDto.getEmail()).isPresent());
        }

        @Test
        @DisplayName("E01 - 이미 가입된 이메일")
        void join_failed_case1() {
            // 중복가입 되지 않는지 확인
            assertThrows(UserException.class, () -> {
                userService.join(userSignDto);
            }, ApiResultStatus.ALREADY_SIGNED_UP.getMessage());
        }

        @Test
        @DisplayName("E02 - 잘못된 이메일 형식")
        void join_failed_case2() {
            userSignDto.setEmail("abc1234.com");

            // 이메일 형식인지 체크
            Set<ConstraintViolation<UserSignDto>> violations = validator.validate(userSignDto);
            violations.forEach(error -> {
                assertThat(error.getMessage()).isEqualTo("잘못된 이메일 형식입니다.");
            });
        }

        @Test
        @DisplayName("E03 - 잘못된 비밀번호 형식")
        void join_failed_case3() {
            // 비밀번호를 7자리로 변경
            userSignDto.setPassword("1234567");

            // 비밀번호가 8자리 이상인지 체크
            Set<ConstraintViolation<UserSignDto>> violations = validator.validate(userSignDto);
            violations.forEach(error -> {
                assertThat(error.getMessage()).isEqualTo("비밀번호는 8자 이상 이어야 합니다.");
            });
        }
    }
}