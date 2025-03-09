package com.taskbuddy.api.error.exception;

import com.taskbuddy.api.error.ApplicationException;
import com.taskbuddy.api.presentation.ResultCodes;

public class DuplicateUsernameException extends ApplicationException {

  public DuplicateUsernameException(ResultCodes resultCode) {
    super(resultCode);
  }
}
