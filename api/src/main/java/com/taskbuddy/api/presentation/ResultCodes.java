package com.taskbuddy.api.presentation;

import com.taskbuddy.api.error.ErrorResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ResultCodes {
    // Common
    C0000(HttpStatus.OK, "Success"),
    C0001(HttpStatus.CREATED, "Success To Create Data"),


    C1001(HttpStatus.BAD_REQUEST, "invalid parameter state"),

    // Auth


    // Task
    T1001(HttpStatus.NOT_FOUND, "task not found"),

    ;

    @Getter
    final HttpStatus status;
    final String description;

    public ErrorResponse toResponse() {
        return new ErrorResponse(this.name());
    }
}