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

    // 401 errors
    UNAUTHORIZED("40100", "허가되지 않는 접근입니다.", HttpStatus.UNAUTHORIZED),

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
