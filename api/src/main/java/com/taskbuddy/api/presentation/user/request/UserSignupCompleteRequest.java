package com.taskbuddy.api.presentation.user.request;

import jakarta.validation.constraints.NotBlank;

public record UserSignupCompleteRequest(@NotBlank String verificationCode) {
}
