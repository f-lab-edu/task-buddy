package com.taskbuddy.api.error;

import com.taskbuddy.api.presentation.ResultCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiControllerAdvice {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException exception) {
        log.error("{} occurred : {}", exception.getClass().getName(), exception.getMessage());
        final ResultCodes resultCode = ResultCodes.C1001;

        return ResponseEntity
                .status(resultCode.getStatus())
                .body(resultCode.toResponse());
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException exception) {
        log.error("{} occurred : {}", exception.getClass().getName(), exception.getMessage());
        final ResultCodes resultCode = exception.getResultCode();

        return ResponseEntity
                .status(resultCode.getStatus())
                .body(resultCode.toResponse());
    }
}
