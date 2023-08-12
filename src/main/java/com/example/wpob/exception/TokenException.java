package com.example.wpob.exception;

public class TokenException extends UnauthorizedException {

    public TokenException(String message) {
        super(message);
    }

    public TokenException(ApiResultStatus apiResultStatus) {
        super(apiResultStatus);
    }

    public TokenException(ApiResultStatus apiResultStatus, String description) {
        super(apiResultStatus, description);
    }

}
