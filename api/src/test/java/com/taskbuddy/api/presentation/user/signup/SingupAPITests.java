package com.taskbuddy.api.presentation.user.signup;

import com.taskbuddy.api.presentation.MySqlTestContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@DisplayName("[회원가입 API] POST /signup")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({RestDocumentationExtension.class})
public class SingupAPITests implements MySqlTestContainer {
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
