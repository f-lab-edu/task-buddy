package com.taskbuddy.api.presentation.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taskbuddy.api.business.user.User;
import com.taskbuddy.api.business.user.UserTokenAuthenticateHandler;
import com.taskbuddy.api.config.PropertiesServer;
import com.taskbuddy.api.presentation.MySqlTestContainer;
import com.taskbuddy.api.presentation.SpringTestContainer;
import com.taskbuddy.api.presentation.secure.SecureDataDecryptor;
import com.taskbuddy.api.presentation.user.request.UserSigninRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

// TODO #signup 테스트 분리
@Disabled
public class UserAuthenticationControllerTest implements SpringTestContainer, MySqlTestContainer {
    @Autowired
    private WebTestClient webTestClient;

    @LocalServerPort
    private int port;

    private ObjectMapper objectMapper;

    @MockBean
    private PropertiesServer propertiesServer;

    @MockBean
    private SecureDataDecryptor secureDataDecryptor;

    @MockBean
    private UserTokenAuthenticateHandler userTokenAuthenticateHandler;

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
        this.objectMapper = objectMapper;
    }

    @Test
    void username과_password로_로그인하고_인증정보를_생성할_수_있다() throws JsonProcessingException {
        UserSigninRequest request = new UserSigninRequest("testuser", "abcdabcd");

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getEmail()).thenReturn("testuser@gmail.com");
        when(mockUser.getUsername()).thenReturn(request.username());
        when(mockUser.getCreatedAt()).thenReturn(LocalDateTime.now());

//        when(clientSecureDataHandler.decode(request, request.getClass())).thenReturn(request.getClass());
//        when(userService.findByUsernameAndPassword(request.username(), request.password())).thenReturn(Optional.of(mockUser));
//        when(userTokenAuthenticateHandler.issueAccessToken(mockUser)).thenReturn("test_accessToken");
//        when(userTokenAuthenticateHandler.issueRefreshToken(mockUser)).thenReturn("test_refreshToken");

        webTestClient.post()
                .uri("/v1/users/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("v1/users/signin/success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("username").type(JsonFieldType.STRING).description("영문 유저네임"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("로그인 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("엑세스토큰"),
                                fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레시토큰"),
                                fieldWithPath("issuedAt").type(JsonFieldType.STRING).description("발급일시 (yyyy-MM-dd)"),
                                fieldWithPath("expiredAt").type(JsonFieldType.STRING).description("만료일시 (yyyy-MM-dd)")
                        )));
    }

    @Test
    void 리프레시토큰으로_로그아웃하여_인증정보를_만료할_수_있다() {
        when(userTokenAuthenticateHandler.isValidRefreshToken(anyString())).thenReturn(true);
        doNothing().when(userTokenAuthenticateHandler).expireAuthentication(anyString());

        webTestClient.post()
                .uri("/v1/users/signout")
                .header(HttpHeaders.AUTHORIZATION, "test_refreshToken")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("v1/users/signout/success",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("리프레시토큰")
                        )));
    }
}
