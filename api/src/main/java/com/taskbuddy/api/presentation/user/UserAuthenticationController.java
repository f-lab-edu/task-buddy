package com.taskbuddy.api.presentation.user;

import com.taskbuddy.api.business.user.User;
import com.taskbuddy.api.business.user.UserCreate;
import com.taskbuddy.api.business.user.UserService;
import com.taskbuddy.api.config.PropertiesServer;
import com.taskbuddy.api.presentation.secure.ClientSecureDataHandler;
import com.taskbuddy.api.presentation.user.request.UserSignupRequest;
import com.taskbuddy.api.presentation.user.response.UserSignupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

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
    public ResponseEntity<Void> signin() {

        return ResponseEntity.ok().build();
    }

    @PostMapping("/signout")
    public ResponseEntity<Void> signout() {

        return ResponseEntity.ok().build();
    }
}
