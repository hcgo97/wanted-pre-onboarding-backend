package com.example.wpob.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserException extends BusinessException {

    private String code;
    private String message;
    private String description;
    private HttpStatus httpStatus;

    public UserException() {
    }

    public UserException(String message) {
        super(message);
    }

    public UserException(ApiResultStatus apiResultStatus) {
        this.code = apiResultStatus.getCode();
        this.message = apiResultStatus.getMessage();
        this.httpStatus = apiResultStatus.getHttpStatus();
        this.description = apiResultStatus.toString();
    }

    public UserException(ApiResultStatus apiResultStatus, String message) {
        this(apiResultStatus);
        this.message = message;
    }

}
