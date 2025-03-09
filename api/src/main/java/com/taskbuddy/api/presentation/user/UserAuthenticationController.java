package com.taskbuddy.api.presentation.user;

import com.taskbuddy.api.business.user.User;
import com.taskbuddy.api.business.user.DefaultUserService;
import com.taskbuddy.api.business.user.UserTokenAuthenticateHandler;
import com.taskbuddy.api.presentation.secure.ClientSecureDataHandler;
import com.taskbuddy.api.presentation.user.request.UserSigninRequest;
import com.taskbuddy.api.presentation.user.response.UserSigninResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Validated
@RequiredArgsConstructor
@RequestMapping("/v1/users")
@RestController
public class UserAuthenticationController {
    private final DefaultUserService userService;
    private final ClientSecureDataHandler clientSecureDataHandler;
    private final UserTokenAuthenticateHandler userTokenAuthenticateHandler;

    @PostMapping("/signin")
    public ResponseEntity<UserSigninResponse> signin(@RequestBody @Valid UserSigninRequest request) {
        final String password = clientSecureDataHandler.decode(request.password(), String.class);

        final Optional<User> optionalUser = userService.findByUsernameAndPassword(request.username(), password);
        if (optionalUser.isEmpty()) {
            // 실패
        }

        final User user = optionalUser.get();
        final String refreshToken = userTokenAuthenticateHandler.issueRefreshToken(user);
        final String accessToken = userTokenAuthenticateHandler.issueAccessToken(user);

        // 임시
        LocalDateTime issuedAt = LocalDateTime.now();
        LocalDateTime expiredAt = LocalDateTime.now().plusDays(10);

        return ResponseEntity.ok(new UserSigninResponse(accessToken, refreshToken, issuedAt, expiredAt));
    }

    @PostMapping("/signout")
    public ResponseEntity<Void> signout(@RequestHeader(name = HttpHeaders.AUTHORIZATION) @NotBlank String refreshToken) {
        boolean isValid = userTokenAuthenticateHandler.isValidRefreshToken(refreshToken);
        if (!isValid) {
            // 실패
        }

        userTokenAuthenticateHandler.expireAuthentication(refreshToken);

        return ResponseEntity.ok()
                .build();
    }
}
