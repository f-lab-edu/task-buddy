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
    A0001(HttpStatus.CONFLICT, "Already Exists"),
    A0002(HttpStatus.NOT_ACCEPTABLE, "invalid secret key"),

    // User
    U1001(HttpStatus.CONFLICT, "duplicated email"),
    U1002(HttpStatus.CONFLICT, "duplicated username"),
    U1003(HttpStatus.INTERNAL_SERVER_ERROR, "failed to send email"),
    U1004(HttpStatus.BAD_REQUEST, "expired to verify email"),
    U1005(HttpStatus.BAD_REQUEST, "Does Not Match Verification Code"),

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
