package com.taskbuddy.api.presentation.user.response;

import java.time.LocalDateTime;

public record UserSigninResponse(String accessToken,
                                 String refreshToken,
                                 LocalDateTime issuedAt,
                                 LocalDateTime expiredAt) {
}
