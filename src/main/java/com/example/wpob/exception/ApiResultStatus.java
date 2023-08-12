package com.example.wpob.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ApiResultStatus {

    REQUEST_SUCCESS("20000", "정상적으로 처리되었습니다.", HttpStatus.OK),

    // 400 errors
    CLIENT_ERROR("40000", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),

    // 토큰 관련
    TOKEN_NOT_FOUND("E4011", "토큰을 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("E4012", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_DATE_EXPIRED("E4013", "토큰이 만료되었습니다. 보안을 위해 다시 로그인 해주세요.", HttpStatus.UNAUTHORIZED),
    TOKEN_CREATED_FAILED("E9200", "토큰 생성 중 에러가 발생하였습니다. 다시 시도해 주세요.", HttpStatus.INTERNAL_SERVER_ERROR),

    // 403 errors
    FORBIDDEN("40300", "해당 권한은 호출이 불가합니다.", HttpStatus.FORBIDDEN),

    // 404 errors
    NOT_FOUND("40400", "해당 데이터 또는 경로를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // server error (90000~)
    INTERNAL_SERVER_ERROR("90000", "서버에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_ACCESS_ERROR("90001", "Database Access 중 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    }
