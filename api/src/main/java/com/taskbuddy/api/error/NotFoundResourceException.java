package com.taskbuddy.api.error;

import lombok.Getter;

@Getter
public class NotFoundResourceException extends RuntimeException {
    private final String code;

    public NotFoundResourceException(String code, String message) {
        super(message);
        this.code = code;
    }
}
