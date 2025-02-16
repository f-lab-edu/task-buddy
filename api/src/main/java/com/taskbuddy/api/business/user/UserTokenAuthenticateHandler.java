package com.taskbuddy.api.business.user;

import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

@Service
public class UserTokenAuthenticateHandler {

    public String issueRefreshToken(User user) {
        return null;
    }

    public String issueAccessToken(User user) {
        return null;
    }

    public boolean isValidRefreshToken(@NotBlank String refreshToken) {
        return false;
    }


    public void expireAuthentication(@NotBlank String refreshToken) {
        // access, refresh 모두 만료
    }
}
