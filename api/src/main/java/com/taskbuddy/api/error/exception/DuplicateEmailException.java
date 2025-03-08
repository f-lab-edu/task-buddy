package com.taskbuddy.api.error.exception;

import com.taskbuddy.api.error.ApplicationException;
import com.taskbuddy.api.presentation.ResultCodes;

public class DuplicateEmailException extends ApplicationException {

    public DuplicateEmailException(ResultCodes resultCode) {
        super(resultCode);
    }
}
