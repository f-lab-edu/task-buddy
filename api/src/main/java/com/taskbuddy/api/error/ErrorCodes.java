package com.taskbuddy.api.error;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ErrorCodes {
    INVALID_PARAMETER_STATE("1010", "invalid format or parameter")
    ;

    private final String code;
    private final String description;

    public ErrorResponse toResponse() {
        return new ErrorResponse(code);
    }
}
