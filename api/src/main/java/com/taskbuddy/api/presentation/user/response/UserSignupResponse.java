package com.taskbuddy.api.presentation.user.response;

import com.taskbuddy.api.business.user.User;

import java.time.LocalDateTime;

public record UserSignupResponse(
        String email,
        String username,
        LocalDateTime createdAt
) {

    public static UserSignupResponse from(User user) {
        return new UserSignupResponse(user.getEmail(), user.getUsername(), user.getCreatedAt());
    }
}
