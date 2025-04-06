package com.taskbuddy.api.business.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignupCache(
        @NotNull String verificationCode,
        @NotBlank String email,
        @NotBlank String username,
        @NotBlank String password
) {
}
