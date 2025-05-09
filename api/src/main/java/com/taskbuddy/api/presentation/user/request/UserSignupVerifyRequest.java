package com.taskbuddy.api.presentation.user.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;

import java.util.regex.Pattern;

import static org.springframework.util.StringUtils.hasText;

public record UserSignupVerifyRequest(
        @NotBlank String email,
        @NotBlank String username,
        @NotBlank String password
) {
    private static final String EMAIL_REGEX = "^[^@]+@[^@]+\\.[^@]+$";
    private static final String USERNAME_REGEX = "^[A-Za-z]{3}[A-Za-z0-9_]{2,17}$";
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+~\\-={}\\[\\]:;\"'<>,.?\\/]).{5,20}$";

    @JsonIgnore
    public boolean isValid() {
        return hasText(email)
                && hasText(username)
                && hasText(password)
                && Pattern.matches(EMAIL_REGEX, email)
                && Pattern.matches(USERNAME_REGEX, username)
                && Pattern.matches(PASSWORD_REGEX, password);
    }
}
