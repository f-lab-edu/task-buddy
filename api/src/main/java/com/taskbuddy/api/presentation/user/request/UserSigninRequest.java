package com.taskbuddy.api.presentation.user.request;

import jakarta.validation.constraints.NotBlank;

public record UserSigninRequest(@NotBlank String username,
                                @NotBlank String password) {
}
