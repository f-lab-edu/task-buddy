package com.taskbuddy.api.presentation.user.signup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taskbuddy.api.business.user.dto.SignupCache;
import com.taskbuddy.api.persistence.cache.CacheKeys;
import com.taskbuddy.api.persistence.cache.CacheManager;
import com.taskbuddy.api.presentation.MySqlTestContainer;
import com.taskbuddy.api.presentation.RedisTestContainer;
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

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.taskbuddy.api.presentation.user.SignupController.SESSION_KEY_NAME;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

@DisplayName("[회원가입 API] POST /signup")
public class SignupAPITests implements SpringTestContainer, MySqlTestContainer, RedisTestContainer {
    @Autowired
    private WebTestClient client;

    @LocalServerPort
    private int port;

    private static final String API_PATH = "/v1/signup";

    @Autowired
    CacheManager cacheManager;

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
        // given
        final String verificationCode = RandomCodeGenerator.generateConsistingOfOnlyNumbers(6);
        final String sessionKey = RandomCodeGenerator.generateConsistingOfLettersAndNumbers(50);

        SignupCache signupCache = new SignupCache(verificationCode, "testuser@email.com", "testuser123", "abcd1234!@#");
        String cacheKey = generateCacheKey(sessionKey, signupCache);
        cacheManager.save(cacheKey, signupCache, Duration.ofMinutes(1));

        UserSignupRequest request = new UserSignupRequest(verificationCode);

        // when & then
        client.post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .header(SESSION_KEY_NAME, sessionKey)
                .bodyValue(JsonUtils.serialize(request))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .consumeWith(document("v1/signup/success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(SESSION_KEY_NAME).description("생성된 세션 키")
                        ),
                        requestFields(
                                fieldWithPath("verificationCode").type(JsonFieldType.STRING).description("이메일 인증코드")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("생성된 계정의 이메일"),
                                fieldWithPath("username").type(JsonFieldType.STRING).description("생성된 계정의 유저네임"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성일시")
                        )));
    }

    @Test
    void 인증기한이_지나서_회원가입_인증했다면_400응답이어야_한다() {
        // given
        final String verificationCode = RandomCodeGenerator.generateConsistingOfOnlyNumbers(6);
        final String sessionKey = RandomCodeGenerator.generateConsistingOfLettersAndNumbers(50);

        UserSignupRequest request = new UserSignupRequest(verificationCode);

        // when & then
        client.post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .header(SESSION_KEY_NAME, sessionKey)
                .bodyValue(JsonUtils.serialize(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(document("v1/signup/failure/expire-verification",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(SESSION_KEY_NAME).description("생성된 세션 키")
                        ),
                        requestFields(
                                fieldWithPath("verificationCode").type(JsonFieldType.STRING).description("이메일 인증코드")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        RestDocsSnippetProvider.errorResponseFields()));
    }

    @Test
    void 일치하지_않는_인증코드로_회원가입_인증했다면_400응답이어야_한다() {
        // given
        final String verificationCode = RandomCodeGenerator.generateConsistingOfOnlyNumbers(6);
        final String sessionKey = RandomCodeGenerator.generateConsistingOfLettersAndNumbers(50);

        SignupCache signupCache = new SignupCache(verificationCode, "testuser@email.com", "testuser123", "abcd1234!@#");
        String cacheKey = generateCacheKey(sessionKey, signupCache);
        cacheManager.save(cacheKey, signupCache, Duration.ofMinutes(1));

        final String requestVerificationCode = RandomCodeGenerator.generateConsistingOfOnlyNumbers(6);
        UserSignupRequest request = new UserSignupRequest(requestVerificationCode);

        assert !verificationCode.equals(requestVerificationCode) : "verficiationCode와 requestVerificationCode는 일치하지 않아야한다.";

        // when & then
        client.post()
                .uri(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .header(SESSION_KEY_NAME, sessionKey)
                .bodyValue(JsonUtils.serialize(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(document("v1/signup/failure/does-not-match-verification_code",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(SESSION_KEY_NAME).description("생성된 세션 키")
                        ),
                        requestFields(
                                fieldWithPath("verificationCode").type(JsonFieldType.STRING).description("이메일 인증코드")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        RestDocsSnippetProvider.errorResponseFields()));
    }

    private String generateCacheKey(String sessionKey, SignupCache signupCache) {
        Map<String, String> argMap = new HashMap<>();
        argMap.put("SESSION", sessionKey);
        argMap.put("EMAIL", signupCache.email());
        argMap.put("USERNAME", signupCache.username());

        return CacheKeys.SIGNUP_VERIFICATION.generate(argMap);
    }
}
