package com.taskbuddy.api.error;

import com.taskbuddy.api.presentation.ResultCodes;
import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {
    protected final ResultCodes resultCode;

    public ApplicationException(ResultCodes resultCode) {
        this.resultCode = resultCode;
    }

    public ApplicationException(ResultCodes resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }
}
