package com.example.wpob.service;

import com.example.wpob.dto.post.PostEditDto;
import com.example.wpob.entity.Users;
import com.example.wpob.repository.PostsRepository;
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
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("local")
class PostServiceTest {

    @Autowired
    PostService postService;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    PostsRepository postsRepository;

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

    // 유저 생성 후 리턴
    Users before_create_users() {
        Users user = Users.create("test@a.com", "test1234");
        usersRepository.save(user);

        return user;
    }

    @Nested
    @DisplayName("파라미터 유효성 검증 테스트")
    @Transactional
    class validation_test {

        @Test
        @DisplayName("잘못된 제목 형식")
        void title_check() {
            PostEditDto postEditDto = PostEditDto.builder()
                    .title("1234567890101010")
                    .contents("test contents")
                    .build();

            // 제목이 10자 이하인지 체크
            Set<ConstraintViolation<PostEditDto>> violations = validator.validate(postEditDto);
            violations.forEach(error -> {
                assertThat(error.getMessage()).isIn("제목은 10자 이하로 입력하여야 합니다.");
            });
        }

        @Test
        @DisplayName("잘못된 내용 형식")
        void contents_check() {
            PostEditDto postEditDto = PostEditDto.builder()
                    .title("test title")
                    .contents("봄이 오면 내린 첫 비는 상쾌하게 땅을 촉촉하게 적시고," +
                            "꽃들은 다채롭게 피어나기 시작합니다. 바람은 부드럽게 불어와 " +
                            "마음을 가라앉히고, 신선한 공기는 마음을 맑게 만듭니다. 산책하는 " +
                            "사람들의 웃음소리와 새들의 노래가 공중에 퍼져 나타나는 봄은 참 " +
                            "아름다운 계절입니다.\\n봄은 새로운 시작을 상징합니다. 추운 겨울을" +
                            " 지나 따뜻한 햇살이 새로운 에너지를 불어넣어 줍니다. 학생들은 새 " +
                            "학기를 시작하며 새로운 목표와 꿈을 품고 공부에 임하게 됩니다. 농부들은 " +
                            "농작물을 심고 기다렸던 수확의 계절을 기다립니다.\\n봄은 자연이 다시" +
                            " 깨어나는 시간이기도 합니다. 나무들은 싹을 틔우며 새록새록 노란 잎새를 " +
                            "펴내고, 꽃들은 아름다운 색깔로 피어납니다. 이 모든 것들이 함께하면서 " +
                            "자연은 환상적인 풍경을 선사합니다.\\n따뜻한 날씨와 함께 시작되는 봄은 " +
                            "사람들에게 활기를 불어넣어 줍니다. 야외 활동을 즐기며 자연을 만끽하고," +
                            " 새로운 계획을 세우며 새로운 도전에 나설 수 있는 기회를 제공합니다. 봄은 " +
                            "어떠한 이유로든 우리에게 희망과 기대를 안겨주는 아름다운 계절입니다.asdfasfadsfdasf")
                    .build();

            // 제목이 1자 이상 10자 이하인지 체크
            Set<ConstraintViolation<PostEditDto>> violations = validator.validate(postEditDto);
            violations.forEach(error -> {
                assertThat(error.getMessage()).isIn("내용은 300자 이하로 입력하여야 합니다.");
            });
        }
    }


    @Nested
    @DisplayName("게시글 작성 테스트")
    @Transactional
    class create_post_test {

        @Test
        @DisplayName("S01 - 게시글 작성 성공")
        void create_post_success_case1() {
            Users users = before_create_users();

            PostEditDto postEditDto = PostEditDto.builder()
                    .title("test title")
                    .contents("test contents")
                    .build();

            assertDoesNotThrow(() -> {
                // 게시글 작성
                postService.createPost(postEditDto, users);
                // 정상 생성 되었는지 조회
                assertTrue(postsRepository.countByUsers_IdAndIsDeletedIsFalse(users.getId()) > 0);
            });
        }
    }

}