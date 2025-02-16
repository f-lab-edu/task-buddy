package com.taskbuddy.api.presentation.user;

import com.taskbuddy.api.business.user.User;
import com.taskbuddy.api.business.user.UserCreate;
import com.taskbuddy.api.business.user.UserService;
import com.taskbuddy.api.config.PropertiesServer;
import com.taskbuddy.api.presentation.secure.ClientSecureDataHandler;
import com.taskbuddy.api.presentation.user.request.UserSigninRequest;
import com.taskbuddy.api.presentation.user.request.UserSignupRequest;
import com.taskbuddy.api.presentation.user.response.UserSigninResponse;
import com.taskbuddy.api.presentation.user.response.UserSignupResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

@Validated
@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserAuthenticationController {
    private final UserService userService;
    private final PropertiesServer propertiesServer;
    private final ClientSecureDataHandler clientSecureDataHandler;

    @PostMapping("/signup")
    public ResponseEntity<UserSignupResponse> signup(@RequestBody UserSignupRequest request) {
        request.validateIfAllFieldsAreNotEmpty();
        request.validateIfEmailIsValid();
        final String password = clientSecureDataHandler.decode(request.password());
        request.validateIfPasswordIsValid();

        final UserCreate userCreate = new UserCreate(request.email(), request.username(), password);

        // presentation 단계에서 검증을 거친 값들을 전달
        final User user = userService.createAndSave(userCreate);

        return ResponseEntity
                .created(URI.create(propertiesServer.getHostname() + ":" + propertiesServer.getPort() + "/users/details/" + user.getId()))
                .body(UserSignupResponse.from(user));
    }

    @PostMapping("/signin")
    public ResponseEntity<UserSigninResponse> signin(@RequestBody @Valid UserSigninRequest request) {
        /**
         * 비밀번호 디코딩
         *
         * 엑세스토큰 발급
         * 리프레시토큰 발급
         */

        final String password = clientSecureDataHandler.decode(request.password());

        final Optional<User> optionalUser = userService.findByUsernameAndPassword(request.username(), password);
        if (optionalUser.isEmpty()) {
            // 실패
        }

        final User user = optionalUser.get();
        final String refreshToken = refreshTokenIssure.issue(user);
        final String accessToken = accessTokenIssuer.issue(user);

        // 임시
        LocalDateTime issuedAt = LocalDateTime.now();
        LocalDateTime expiredAt = LocalDateTime.now().plusDays(10);

        return ResponseEntity.ok(new UserSigninResponse(accessToken, refreshToken, issuedAt, expiredAt));
    }

    @PostMapping("/signout")
    public ResponseEntity<Void> signout() {

        return ResponseEntity.ok().build();
    }
}
