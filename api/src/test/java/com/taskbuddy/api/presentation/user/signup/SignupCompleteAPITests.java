package com.taskbuddy.api.presentation.user.signup;

import com.taskbuddy.api.presentation.MySqlTestContainer;
import com.taskbuddy.api.presentation.SpringTestContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

@DisplayName("[회원가입 API] POST /signup/complete")
public class SignupCompleteAPITests implements SpringTestContainer, MySqlTestContainer {
    @Autowired
    private WebTestClient client;

    @LocalServerPort
    private int port;

    @Test
    void Cookie에_인증코드가_존재하지_않은다면_실패응답을_반환한다() {
        
    }
}
