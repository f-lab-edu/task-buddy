package com.taskbuddy.api.presentation.user.signup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taskbuddy.api.presentation.MySqlTestContainer;
import com.taskbuddy.api.presentation.SpringTestContainer;
import com.taskbuddy.api.presentation.user.request.UserSignupRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

@DisplayName("[회원가입 API] POST /signup")
public class SingupAPITests implements SpringTestContainer, MySqlTestContainer {
    @Autowired
    private WebTestClient client;

    @LocalServerPort
    private int port;

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
    void 회원가입으로_유저를_생성할_수_있다() throws JsonProcessingException {
        UserSignupRequest request = new UserSignupRequest("testuser@gmail.com", "testuser", "abcdabcd");

//        User mockUser = mock(User.class);
//        when(mockUser.getId()).thenReturn(1L);
//        when(mockUser.getEmail()).thenReturn(request.email());
//        when(mockUser.getUsername()).thenReturn(request.username());
//        when(mockUser.getCreatedAt()).thenReturn(LocalDateTime.now());
//
//        when(clientSecureDataHandler.decode(request.password())).thenReturn(request.password());
//        when(userService.createAndSave(any(UserCreate.class))).thenReturn(mockUser);
//        when(propertiesServer.getHostname()).thenReturn("localhost");
//        when(propertiesServer.getPort()).thenReturn(8888);
//
//        webTestClient.post()
//                .uri("/v1/users/signup")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(JsonUtils.serialize(request))
//                .exchange()
//                .expectStatus().isCreated()
//                .expectBody()
//                .consumeWith(document("v1/users/signup/success",
//                        requestHeaders(
//                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
//                        ),
//                        requestFields(
//                                fieldWithPath("email").type(JsonFieldType.STRING).description("가입 이메일 (이메일 형식과 일치해야한다.)"),
//                                fieldWithPath("username").type(JsonFieldType.STRING).description("영문 유저네임"),
//                                fieldWithPath("password").type(JsonFieldType.STRING).description("로그인 비밀번호")
//                        ),
//                        responseHeaders(
//                                headerWithName(HttpHeaders.LOCATION).description("생성된 User 조회 URL")
//                        ),
//                        responseFields(
//                                fieldWithPath("email").type(JsonFieldType.STRING).description("가입 이메일"),
//                                fieldWithPath("username").type(JsonFieldType.STRING).description("영문 유저네임"),
//                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성일시 (yyyy-MM-dd)")
//                        )));
    }

    @Test
    void 요청파라미터가_비밀키로_복호화가_되지_않는다면_406응답이어야_한다() {

    }

    @Test
    void 유효하지_않은_요청파라미터라면_API응답_상태코드는_400이어야_한다() {

    }

    @Test
    void 요청_이메일로_이미_사용중인_계정이_존재한다면_API응답_상태코드는_409이어야_한다() {

    }

    @Test
    void 요청파라미터가_유효성검증에_통과했다면_API응답_상태코드는_200이어야_한다() {

    }
}
