package com.example.wpob.exception;

import com.example.wpob.dto.ApiResponse;
import com.example.wpob.util.TxidGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RequiredArgsConstructor
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final TxidGenerator txidGenerator;

    /**
     * 400 에러
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse> handleBadRequestException(BadRequestException ex) {
        log.error("Bad Request Exception");
        ex.printStackTrace();

        ApiResponse errorResponse = ApiResponse.SetErrorStatus.builder()
                .txid(txidGenerator.getTxid())
                .status(HttpStatus.BAD_REQUEST)
                .message(ApiResultStatus.CLIENT_ERROR.getMessage())
                .code(ApiResultStatus.CLIENT_ERROR.getCode())
                .description(ApiResultStatus.CLIENT_ERROR.toString())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 404 에러
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("Data Not Found Exception");
        ex.printStackTrace();

        ApiResponse errorResponse = ApiResponse.SetErrorStatus.builder()
                .txid(txidGenerator.getTxid())
                .status(HttpStatus.NOT_FOUND)
                .message(ApiResultStatus.NOT_FOUND.getMessage())
                .code(ApiResultStatus.NOT_FOUND.getCode())
                .description(ApiResultStatus.NOT_FOUND.toString())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * 인증(401) 관련 에러 처리
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse> handleUnauthorizedException(UnauthorizedException ex) {
        log.error("Unauthorized Exception - {}", ex.getCode() + "," + ex.getMessage() + "," + ex.getDescription());
        ex.printStackTrace();

        ApiResponse errorResponse = ApiResponse.SetErrorStatus.builder()
                .txid(txidGenerator.getTxid())
                .status(HttpStatus.UNAUTHORIZED)
                .message(ApiResultStatus.UNAUTHORIZED.getMessage())
                .code(ApiResultStatus.UNAUTHORIZED.getCode())
                .description(ApiResultStatus.UNAUTHORIZED.toString())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 인가(403) 관련 에러 처리
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse> handleForbiddenException(ForbiddenException ex) {
        log.error("Forbidden Exception - {}", ex.getCode() + "," + ex.getMessage() + "," + ex.getDescription());
        ex.printStackTrace();

        ApiResponse errorResponse = ApiResponse.SetErrorStatus.builder()
                .txid(txidGenerator.getTxid())
                .status(HttpStatus.FORBIDDEN)
                .message(ApiResultStatus.FORBIDDEN.getMessage())
                .code(ApiResultStatus.FORBIDDEN.getCode())
                .description(ApiResultStatus.FORBIDDEN.toString())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * DB 관련 에러 처리
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse> handleDataAccessException(DataAccessException ex) {
        log.error("DataAccess Exception");
        ex.printStackTrace();

        ApiResponse errorResponse = ApiResponse.SetErrorStatus.builder()
                .txid(txidGenerator.getTxid())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(ApiResultStatus.DATABASE_ACCESS_ERROR.getMessage())
                .code(ApiResultStatus.DATABASE_ACCESS_ERROR.getCode())
                .description(ApiResultStatus.DATABASE_ACCESS_ERROR.toString())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 비즈니스 예외처리
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse> handleBusinessException(BusinessException ex) {
        log.error("Business Exception - {}", ex.getCode() + "," + ex.getMessage() + "," + ex.getDescription());

        ApiResponse errorResponse = ApiResponse.SetErrorStatus.builder()
                .txid(txidGenerator.getTxid())
                .status(ex.getHttpStatus())
                .message(ex.getMessage())
                .code(ex.getCode())
                .description(ex.getDescription())
                .build();

        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    /**
     * 서버 에러
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception ex) {
        log.error("Internal Server Error Exception");
        log.error(ex.getMessage());

        ex.printStackTrace();

        ApiResponse errorResponse = ApiResponse.SetErrorStatus.builder()
                .txid(txidGenerator.getTxid())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(ApiResultStatus.INTERNAL_SERVER_ERROR.getMessage())
                .code(ApiResultStatus.INTERNAL_SERVER_ERROR.getCode())
                .description(ApiResultStatus.INTERNAL_SERVER_ERROR.toString())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
