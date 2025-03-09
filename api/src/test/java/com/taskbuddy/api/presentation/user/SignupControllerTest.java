package com.taskbuddy.api.presentation.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taskbuddy.api.presentation.MySqlTestContainer;
import com.taskbuddy.api.presentation.SpringTestContainer;
import com.taskbuddy.api.presentation.secure.ClientSecureDataHandler;
import com.taskbuddy.api.presentation.user.request.UserSignupRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

class SignupControllerTest implements SpringTestContainer, MySqlTestContainer {

    @Autowired
    private WebTestClient webTestClient;

    @LocalServerPort
    private int port;

    private ClientSecureDataHandler clientSecureDataHandler = mock(ClientSecureDataHandler.class);

    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentation) {
        this.webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void 회원가입을_요청할_수_있다() {
        String encodedRequest = "encodedRequest";
        UserSignupRequest request = new UserSignupRequest("testuser12@gmail.com", "testuser12", "abcd1234!@");

        when(clientSecureDataHandler.decode(encodedRequest, UserSignupRequest.class)).thenReturn(request);

//        String sessionKey = Math.random();
//        SignupSession session = new SignupSession(sessionKey);
//        when()

        webTestClient.post()
                .uri("/v1/users/signout")
                .header(HttpHeaders.AUTHORIZATION, "test_refreshToken")
                .exchange()
                .expectStatus().isOk();

    }
}
