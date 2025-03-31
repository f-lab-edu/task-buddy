package com.taskbuddy.api.presentation.user;

import com.taskbuddy.api.business.user.SignupService;
import com.taskbuddy.api.business.user.User;
import com.taskbuddy.api.business.user.dto.SignupSession;
import com.taskbuddy.api.config.PropertiesServer;
import com.taskbuddy.api.error.ApplicationException;
import com.taskbuddy.api.presentation.ResultCodes;
import com.taskbuddy.api.presentation.secure.SecureDataDecryptor;
import com.taskbuddy.api.presentation.user.request.UserSignupCompleteRequest;
import com.taskbuddy.api.presentation.user.request.UserSignupRequest;
import com.taskbuddy.api.presentation.user.response.UserSignupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/v1")
@RestController
public class SignupController {
    private final SignupService signupService;
    private final SecureDataDecryptor clientDataDecryptor;
    private final PropertiesServer propertiesServer;

    private static final String SESSION_KEY_NAME = "X-session-key";

    @PostMapping("/signup")
    public ResponseEntity<UserSignupResponse> signup(@RequestBody String encodedRequest) {
        final UserSignupRequest request = clientDataDecryptor.decrypt(encodedRequest, UserSignupRequest.class);

        if (!request.isValid()) {
            throw new ApplicationException(ResultCodes.C1001);
        }

        final SignupSession session = signupService.signup(request);

        return ResponseEntity.ok()
                .header(SESSION_KEY_NAME, session.key())
                .build();
    }

    @PostMapping("/signup/complete")
    public ResponseEntity<UserSignupResponse> signupComplete(@RequestHeader(name = SESSION_KEY_NAME) String sessionKey,
                                                             @RequestBody UserSignupCompleteRequest request) {
        final User user = signupService.signupComplete(sessionKey, request.verificationCode());

        return ResponseEntity
                .created(URI.create(propertiesServer.getHostname() + ":" + propertiesServer.getPort() + "/users/details/" + user.getId()))
                .body(UserSignupResponse.from(user));
    }
}
