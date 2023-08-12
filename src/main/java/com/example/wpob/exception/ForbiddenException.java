package com.example.wpob.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ForbiddenException extends RuntimeException {
    private String code;
    private String message;
    private String description;
    private HttpStatus httpStatus;

    public ForbiddenException() {
    }

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(ApiResultStatus apiResultStatus) {
        this.code = apiResultStatus.getCode();
        this.message = apiResultStatus.getMessage();
        this.httpStatus = apiResultStatus.getHttpStatus();
    }

    public ForbiddenException(ApiResultStatus apiResultStatus, String description) {
        this.code = apiResultStatus.getCode();
        this.message = apiResultStatus.getMessage();
        this.description = description;
        this.httpStatus = apiResultStatus.getHttpStatus();
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
