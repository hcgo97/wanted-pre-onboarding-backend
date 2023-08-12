package com.example.wpob.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UnauthorizedException extends RuntimeException {
    private String code;
    private String message;
    private String description;
    private HttpStatus httpStatus;

    public UnauthorizedException() {
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(ApiResultStatus apiResultStatus) {
        this.code = apiResultStatus.getCode();
        this.message = apiResultStatus.getMessage();
        this.httpStatus = apiResultStatus.getHttpStatus();
    }

    public UnauthorizedException(ApiResultStatus apiResultStatus, String description) {
        this.code = apiResultStatus.getCode();
        this.message = apiResultStatus.getMessage();
        this.description = description;
        this.httpStatus = apiResultStatus.getHttpStatus();
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
