package com.taskbuddy.api.error.exception;

import com.taskbuddy.api.error.ApplicationException;
import com.taskbuddy.api.presentation.ResultCodes;

public class InvalidSecretKeyException extends ApplicationException {

    public InvalidSecretKeyException(ResultCodes resultCode) {
        super(resultCode);
    }
}
