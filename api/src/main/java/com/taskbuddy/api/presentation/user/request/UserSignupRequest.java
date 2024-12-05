package com.taskbuddy.api.presentation.user.request;

public record UserSignupRequest(
        String email,
        String username,
        String password
) {
}
