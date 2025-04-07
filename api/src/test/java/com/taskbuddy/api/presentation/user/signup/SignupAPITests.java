package com.taskbuddy.api.presentation.user.signup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taskbuddy.api.presentation.MySqlTestContainer;
import com.taskbuddy.api.presentation.SpringTestContainer;
import com.taskbuddy.api.presentation.user.request.UserSignupRequest;
import com.taskbuddy.api.utils.JsonUtils;
import com.taskbuddy.api.utils.RandomCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.taskbuddy.api.presentation.user.SignupController.SESSION_KEY_NAME;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

@DisplayName("[회원가입 API] POST /signup")
public class SignupAPITests implements SpringTestContainer, MySqlTestContainer {
    @Autowired
    private WebTestClient client;

    @LocalServerPort
    private int port;

    private static final String API_PATH = "/v1/signup";

    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentation) {
        this.client = WebTestClient.bindToServer()
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
    void 이메일_인증정보가_일치하면_회원가입을_할_수_있다() {
        final String verificationCode = RandomCodeGenerator.generateConsistingOfOnlyNumbers(6);

        UserSignupRequest request = new UserSignupRequest(verificationCode);


        client.post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(JsonUtils.serialize(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("v1/signup/success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("가입 이메일 (이메일 형식과 일치해야한다.)"),
                                fieldWithPath("username").type(JsonFieldType.STRING).description("영문 유저네임"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("로그인 비밀번호")
                        ),
                        responseHeaders(
                                headerWithName(SESSION_KEY_NAME).description("생성된 세션 키")
                        )));
    }

    @Test
    void 인증기한이_지나서_회원가입_인증했다면_400응답이어야_한다() {

    }

    @Test
    void 일치하지_않는_인증코드로_회원가입_인증했다면_400응답이어야_한다() {

    }


}
