package com.taskbuddy.api.controller;

import com.taskbuddy.api.controller.response.ApiResponse;
import com.taskbuddy.api.controller.response.ErrorDetail;
import com.taskbuddy.api.error.NotFoundResourceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiControllerAdvice {

    @ExceptionHandler(NotFoundResourceException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFoundResourceException(NotFoundResourceException exception) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(new ErrorDetail(exception.getCode(), exception.getMessage())));
    }
}
