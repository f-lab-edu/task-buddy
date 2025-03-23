package com.taskbuddy.api.presentation.user.signup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taskbuddy.api.business.user.SignupService;
import com.taskbuddy.api.business.user.dto.SignupSession;
import com.taskbuddy.api.error.exception.DuplicateEmailException;
import com.taskbuddy.api.error.exception.InvalidSecretKeyException;
import com.taskbuddy.api.presentation.MySqlTestContainer;
import com.taskbuddy.api.presentation.ResultCodes;
import com.taskbuddy.api.presentation.SpringTestContainer;
import com.taskbuddy.api.presentation.secure.SecureDataDecryptor;
import com.taskbuddy.api.presentation.user.request.UserSignupRequest;
import com.taskbuddy.api.utils.JsonUtils;
import com.taskbuddy.api.utils.RandomCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

@DisplayName("[회원가입 API] POST /signup")
public class SingupAPITests implements SpringTestContainer, MySqlTestContainer {
    @Autowired
    private WebTestClient client;

    @LocalServerPort
    private int port;

    @MockBean
    private SecureDataDecryptor secureDataDecryptor;

    @MockBean
    private SignupService signupService;

    /**
     * API 명세
     * 회원가입 (POST /signup)
     * - 이메일, 비밀번호, 이름은 Not Null이어야한다. (그렇지 않을 경우 400 응답)
     * - 이메일은 중복되지 않아야한다. (중복될 경우 409 응답)
     * - 정상일 경우 쿠키에 세션키가 있어야하고, 200응답을 전달한다.
     *
     * 세션 (`account::signup::{key}`)
     * - 20자리의 알파벳과 숫자가 섞인 키여야 한다.
     * - 캐시저장소에 세션키가 Key이고, Object에 회원요청정보와 인증코드,시간이 저장되어있어야 한다.
     * - 만료시간은 5분이어야한다.
     *
     * 인증코드 검증 (POST /signup/complete)
     * - 인증코드와 Cookie에 세션키가 존재해야한다.
     * - 세션키로 캐시저장소에서 조회한 것과 일치하는지 확인한다. (일치하지 않을 경우 400 응답)
     * - 유저 저장 & 캐시 무효화
     * - 201 응답
     */

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
    void 회원가입으로_유저를_생성할_수_있다() {
        UserSignupRequest request = new UserSignupRequest("testuser@gmail.com", "TestUser12", "test123456!");

        when(secureDataDecryptor.decrypt(Mockito.any(), Mockito.any())).thenReturn(request);
        final String givenSessionKey = String.valueOf(RandomCodeGenerator.generateConsistingOfLettersAndNumbers(50));
        when(signupService.signup(Mockito.any())).thenReturn(new SignupSession(givenSessionKey));

        client.post()
                .uri("/v1/signup")
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
                                headerWithName(HttpHeaders.SET_COOKIE).description("생성된 세션 키")
                        )));
    }

    @Test
    void 요청파라미터가_비밀키로_복호화가_되지_않는다면_406응답이어야_한다() {
        UserSignupRequest request = new UserSignupRequest("testuser@gmail.com", "TestUser12", "test123456!");

        when(secureDataDecryptor.decrypt(Mockito.any(), Mockito.any())).thenReturn(request);
        when(secureDataDecryptor.decrypt(Mockito.any(), Mockito.any())).thenThrow(new InvalidSecretKeyException(ResultCodes.A0002));

        client.post()
                .uri("/v1/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(JsonUtils.serialize(request))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .consumeWith(document("v1/signup/failure/decrypt",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("가입 이메일 (이메일 형식과 일치해야한다.)"),
                                fieldWithPath("username").type(JsonFieldType.STRING).description("영문 유저네임"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("로그인 비밀번호")
                        ),
                        RestDocsSnippetProvider.errorResponseFields()));
    }

    @Test
    void 유효하지_않은_요청파라미터라면_API응답_상태코드는_400이어야_한다() {
        UserSignupRequest request = new UserSignupRequest("testuser@gmail.com", "TestUser12", "test123456!");

        UserSignupRequest mockRequest = Mockito.mock(UserSignupRequest.class);
        when(secureDataDecryptor.decrypt(Mockito.any(), Mockito.any())).thenReturn(mockRequest);
        when(mockRequest.isValid()).thenReturn(false);

        client.post()
                .uri("/v1/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(JsonUtils.serialize(request))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .consumeWith(document("v1/signup/failure/invalid-parameters",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("가입 이메일 (이메일 형식과 일치해야한다.)"),
                                fieldWithPath("username").type(JsonFieldType.STRING).description("영문 유저네임"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("로그인 비밀번호")
                        ),
                        RestDocsSnippetProvider.errorResponseFields()));
    }

    @Test
    void 요청_이메일로_이미_사용중인_계정이_존재한다면_API응답_상태코드는_409이어야_한다() {
        UserSignupRequest request = new UserSignupRequest("testuser@gmail.com", "TestUser12", "test123456!");

        when(secureDataDecryptor.decrypt(Mockito.any(), Mockito.any())).thenReturn(request);
        when(signupService.signup(Mockito.any())).thenThrow(new DuplicateEmailException(ResultCodes.A0001));

        client.post()
                .uri("/v1/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(JsonUtils.serialize(request))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .consumeWith(document("v1/signup/failure/exists-email",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("가입 이메일 (이메일 형식과 일치해야한다.)"),
                                fieldWithPath("username").type(JsonFieldType.STRING).description("영문 유저네임"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("로그인 비밀번호")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        RestDocsSnippetProvider.errorResponseFields()));
    }
}
