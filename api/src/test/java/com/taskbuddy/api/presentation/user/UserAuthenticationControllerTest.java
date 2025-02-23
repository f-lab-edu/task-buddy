package com.taskbuddy.api.presentation.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taskbuddy.api.business.user.User;
import com.taskbuddy.api.business.user.UserCreate;
import com.taskbuddy.api.business.user.UserService;
import com.taskbuddy.api.business.user.UserTokenAuthenticateHandler;
import com.taskbuddy.api.config.PropertiesServer;
import com.taskbuddy.api.presentation.MySqlTestContainers;
import com.taskbuddy.api.presentation.secure.ClientSecureDataHandler;
import com.taskbuddy.api.presentation.user.request.UserSigninRequest;
import com.taskbuddy.api.presentation.user.request.UserSignupRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({RestDocumentationExtension.class})
public class UserAuthenticationControllerTest implements MySqlTestContainers {
    @Autowired
    private WebTestClient webTestClient;

    @LocalServerPort
    private int port;

    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private PropertiesServer propertiesServer;

    @MockBean
    private ClientSecureDataHandler clientSecureDataHandler;

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
    void 회원가입으로_유저를_생성할_수_있다() throws JsonProcessingException {
        UserSignupRequest request = new UserSignupRequest("testuser@gmail.com", "testuser", "abcdabcd");

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getEmail()).thenReturn(request.email());
        when(mockUser.getUsername()).thenReturn(request.username());
        when(mockUser.getCreatedAt()).thenReturn(LocalDateTime.now());

        when(clientSecureDataHandler.decode(request.password())).thenReturn(request.password());
        when(userService.createAndSave(any(UserCreate.class))).thenReturn(mockUser);
        when(propertiesServer.getHostname()).thenReturn("localhost");
        when(propertiesServer.getPort()).thenReturn(8888);

        webTestClient.post()
                .uri("/v1/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .consumeWith(document("v1/users/signup/success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("가입 이메일 (이메일 형식과 일치해야한다.)"),
                                fieldWithPath("username").type(JsonFieldType.STRING).description("영문 유저네임"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("로그인 비밀번호")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("생성된 User 조회 URL")
                        ),
                        responseFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("가입 이메일"),
                                fieldWithPath("username").type(JsonFieldType.STRING).description("영문 유저네임"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성일시 (yyyy-MM-dd)")
                        )));
    }

    @Test
    void username과_password로_로그인하고_인증정보를_생성할_수_있다() throws JsonProcessingException {
        UserSigninRequest request = new UserSigninRequest("testuser", "abcdabcd");

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getEmail()).thenReturn("testuser@gmail.com");
        when(mockUser.getUsername()).thenReturn(request.username());
        when(mockUser.getCreatedAt()).thenReturn(LocalDateTime.now());

        when(clientSecureDataHandler.decode(request.password())).thenReturn(request.password());
        when(userService.findByUsernameAndPassword(request.username(), request.password())).thenReturn(Optional.of(mockUser));
        when(userTokenAuthenticateHandler.issueAccessToken(mockUser)).thenReturn("test_accessToken");
        when(userTokenAuthenticateHandler.issueRefreshToken(mockUser)).thenReturn("test_refreshToken");

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
