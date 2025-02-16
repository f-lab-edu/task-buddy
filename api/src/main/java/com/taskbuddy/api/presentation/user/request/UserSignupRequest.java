package com.taskbuddy.api.presentation.user.request;

import org.springframework.util.StringUtils;

public record UserSignupRequest(
        String email,
        String username,
        String password
) {

    public void validateIfAllFieldsAreNotEmpty() {
        boolean notEmpty = StringUtils.hasText(email) && StringUtils.hasText(username) && StringUtils.hasText(password);
    }

    public void validateIfEmailIsValid() {
        // 이메일 형식에 맞는지
    }

    public void validateIfPasswordIsValid() {
        // 비밀번호 규칙에 맞는지
    }
}
