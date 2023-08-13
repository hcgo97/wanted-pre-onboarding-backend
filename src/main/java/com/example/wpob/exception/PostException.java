package com.example.wpob.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PostException extends BusinessException {

    private String code;
    private String message;
    private String description;
    private HttpStatus httpStatus;

    public PostException() {
    }

    public PostException(String message) {
        super(message);
    }

    public PostException(ApiResultStatus apiResultStatus) {
        this.code = apiResultStatus.getCode();
        this.message = apiResultStatus.getMessage();
        this.httpStatus = apiResultStatus.getHttpStatus();
        this.description = apiResultStatus.toString();
    }

    public PostException(ApiResultStatus apiResultStatus, String message) {
        this(apiResultStatus);
        this.message = message;
    }

}
