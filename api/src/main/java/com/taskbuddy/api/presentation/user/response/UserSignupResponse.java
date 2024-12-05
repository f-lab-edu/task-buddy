package com.taskbuddy.api.presentation.user.response;

public record UserSignupResponse(
        String username,
        String email,
        String createdAt
) {
}
