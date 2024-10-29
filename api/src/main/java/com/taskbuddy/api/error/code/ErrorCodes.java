package com.taskbuddy.api.error.code;

import com.taskbuddy.api.error.ErrorResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ErrorCodes {
    INVALID_PARAMETER_STATE(10),
    ;

    final int code;

    public String code() {
        return String.format("%4d", code);
    }

    public ErrorResponse toResponse() {
        return new ErrorResponse(code());
    }
}
