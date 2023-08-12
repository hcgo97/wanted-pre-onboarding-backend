drop table if exists users;
drop table if exists posts;

CREATE TABLE `users`
(
    `id`         bigint      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `email`      varchar(50) NOT NULL UNIQUE COMMENT '이메일(로그인 ID)',
    `password`   varchar(50) NOT NULL COMMENT '비밀번호(bcrypt)',
    `created_at` datetime COMMENT '생성일자',
    `updated_at` datetime COMMENT '수정일자',
    `is_deleted` tinyint(1) DEFAULT false COMMENT '삭제여부 true/false',
    `deleted_at` datetime   DEFAULT NULL COMMENT '삭제일자'
) ENGINE = InnoDB, COMMENT = '유저 정보';

CREATE TABLE `posts`
(
    `id`         bigint      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`    bigint      NOT NULL COMMENT 'users PK',
    `title`      varchar(20) NOT NULL COMMENT '게시글 제목',
    `contents`   text        NOT NULL COMMENT '게시글 내용',
    `created_at` datetime COMMENT '생성일자',
    `updated_at` datetime COMMENT '수정일자',
    `is_deleted` tinyint(1) DEFAULT false COMMENT '삭제여부 true/false',
    `deleted_at` datetime   DEFAULT NULL COMMENT '삭제일자'
) ENGINE = InnoDB, COMMENT = '게시글 정보';
