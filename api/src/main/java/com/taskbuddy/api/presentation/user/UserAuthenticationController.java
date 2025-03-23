package com.taskbuddy.api.presentation.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RequestMapping("/v1/users")
@RestController
public class UserAuthenticationController {
//    private final SigninService signinService;
//    private final SecureDataDecryptor secureDataDecryptor;
//    private final UserTokenAuthenticateHandler userTokenAuthenticateHandler;
//
//    @PostMapping("/signin")
//    public ResponseEntity<UserSigninResponse> signin(@RequestBody @Valid UserSigninRequest request) {
//        final String password = secureDataDecryptor.decrypt(request.password(), String.class);
//
//        final Optional<User> optionalUser = signinService.findByUsernameAndPassword(request.username(), password);
//        if (optionalUser.isEmpty()) {
//            // 실패
//        }
//
//        final User user = optionalUser.get();
//        final String refreshToken = userTokenAuthenticateHandler.issueRefreshToken(user);
//        final String accessToken = userTokenAuthenticateHandler.issueAccessToken(user);
//
//        // 임시
//        LocalDateTime issuedAt = LocalDateTime.now();
//        LocalDateTime expiredAt = LocalDateTime.now().plusDays(10);
//
//        return ResponseEntity.ok(new UserSigninResponse(accessToken, refreshToken, issuedAt, expiredAt));
//    }
//
//    @PostMapping("/signout")
//    public ResponseEntity<Void> signout(@RequestHeader(name = HttpHeaders.AUTHORIZATION) @NotBlank String refreshToken) {
//        boolean isValid = userTokenAuthenticateHandler.isValidRefreshToken(refreshToken);
//        if (!isValid) {
//            // 실패
//        }
//
//        userTokenAuthenticateHandler.expireAuthentication(refreshToken);
//
//        return ResponseEntity.ok()
//                .build();
//    }
}
