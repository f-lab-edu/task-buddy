package com.taskbuddy.api.business.user.dto;

import com.taskbuddy.api.presentation.user.request.UserSignupRequest;
import jakarta.validation.constraints.NotNull;

public record SignupCache(
        @NotNull Integer verificationCode,
        @NotNull UserSignupRequest request
) {
}
