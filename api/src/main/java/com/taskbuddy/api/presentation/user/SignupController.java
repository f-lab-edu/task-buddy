package com.taskbuddy.api.presentation.user;

import com.taskbuddy.api.business.user.DefaultUserService;
import com.taskbuddy.api.business.user.User;
import com.taskbuddy.api.business.user.UserCreate;
import com.taskbuddy.api.business.user.dto.SignupSession;
import com.taskbuddy.api.config.PropertiesServer;
import com.taskbuddy.api.error.ApplicationException;
import com.taskbuddy.api.presentation.ResultCodes;
import com.taskbuddy.api.presentation.secure.ClientSecureDataHandler;
import com.taskbuddy.api.presentation.user.request.UserSignupRequest;
import com.taskbuddy.api.presentation.user.response.UserSignupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.Duration;

@RequiredArgsConstructor
@RequestMapping("/v1")
@RestController
public class SignupController {
    private final DefaultUserService userService;
    private final ClientSecureDataHandler clientSecureDataHandler;
    private final PropertiesServer propertiesServer;

    private static final String SESSION_KEY_NAME = "x-session-key";

    @PostMapping("/signup")
    public ResponseEntity<UserSignupResponse> signup(@RequestBody String encodedRequest) {
        // 1. Private Key로 암호화
        final UserSignupRequest request = clientSecureDataHandler.decode(encodedRequest, UserSignupRequest.class);

        // 2. 파라미터 유효성 검증
        if (!request.isValid()) {
            throw new ApplicationException(ResultCodes.C1001);
        }

        final SignupSession session = userService.signup(request);

        // 5. 세션 Key 쿠키에 담아 응답
        final ResponseCookie cookie = ResponseCookie.from(SESSION_KEY_NAME, session.key())
                .httpOnly(true)
//                .secure(true) // 주석 - 개발환경
                .maxAge(Duration.ofMinutes(5))
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping("/signup/complete")
    public ResponseEntity<UserSignupResponse> signupComplete(@RequestBody UserSignupRequest request) {
        final UserCreate userCreate = new UserCreate(request.email(), request.username(), request.password());

        // presentation 단계에서 검증을 거친 값들을 전달
        final User user = userService.createAndSave(userCreate);

        return ResponseEntity
                .created(URI.create(propertiesServer.getHostname() + ":" + propertiesServer.getPort() + "/users/details/" + user.getId()))
                .body(UserSignupResponse.from(user));
    }
}
