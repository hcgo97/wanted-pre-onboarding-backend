package com.example.wpob.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ApiResponse {

    private String txid;
    private Integer status;
    private String message;

    @Getter
    public static class SetSuccessStatus<T> extends ApiResponse {
        private final T data;

        @Builder
        public SetSuccessStatus(String txid, HttpStatus status, String message, T data) {
            super(txid, status.value(), message);
            this.data = data;
        }
    }

    @Getter
    public static class SetErrorStatus extends ApiResponse {
        private final ErrorResponse error;

        @Builder
        public SetErrorStatus(String txid, HttpStatus status, String message, String code, String description) {
            super(txid, status.value(), message);
            this.error = ErrorResponse.builder()
                    .code(code)
                    .description(description)
                    .build();
        }
    }
}
