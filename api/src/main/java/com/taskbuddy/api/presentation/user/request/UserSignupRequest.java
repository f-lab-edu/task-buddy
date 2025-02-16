package com.taskbuddy.api.presentation.user.request;

import com.taskbuddy.api.utils.Regexps;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserSignupRequest(
        @NotBlank @Email(regexp = Regexps.EMAIL) String email,
        @NotBlank String username,
        @NotBlank String password
) {

    public void validateIfPasswordIsValid() {
        // 비밀번호 규칙에 맞는지
    }
}
