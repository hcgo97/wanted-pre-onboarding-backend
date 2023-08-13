package com.example.wpob.controller;

import com.example.wpob.dto.ApiResponse;
import com.example.wpob.dto.user.UserAuthDto;
import com.example.wpob.entity.Users;
import com.example.wpob.exception.ApiResultStatus;
import com.example.wpob.repository.UsersRepository;
import com.example.wpob.util.TxidGenerator;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

public class BaseController {

    @Autowired
    private TxidGenerator txidGenerator;

    @Autowired
    private UsersRepository usersRepository;

    protected ResponseEntity<ApiResponse> responseBuilder(Object data, HttpStatus httpStatus) {
        ApiResponse apiResponse = ApiResponse.SetSuccessStatus.builder()
                .txid(txidGenerator.getTxid())
                .status(httpStatus)
                .message(ApiResultStatus.REQUEST_SUCCESS.getMessage())
                .data(data)
                .build();

        return new ResponseEntity<>(apiResponse, httpStatus);
    }

    private UserAuthDto getAuthInfo() {
        return (UserAuthDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    protected Long getUserId() {
        return this.getAuthInfo().getUserId();
    }

    protected String getEmail() {
        return this.getAuthInfo().getEmail();
    }

    protected Users getUsers() {
        return usersRepository.findByIdAndIsDeletedIsFalse(this.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(ApiResultStatus.NOT_FOUND.getMessage()));
    }

}
