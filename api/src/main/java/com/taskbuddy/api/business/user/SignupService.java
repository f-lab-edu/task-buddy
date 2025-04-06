package com.taskbuddy.api.business.user;

import com.taskbuddy.api.business.user.dto.SignupSession;
import com.taskbuddy.api.error.exception.DuplicateEmailException;
import com.taskbuddy.api.error.exception.DuplicateUsernameException;
import com.taskbuddy.api.presentation.user.request.UserSignupRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface SignupService {

    /**
     * 사용자의 가입정보를 검증하고, 인증요청을 한다.
     *
     * @param request 유저 가입 요청 정보 (이메일, 사용자명, 비밀번호 포함)
     * @return {@link SignupSession} 생성된 인증 세션 키 (쿠키에 저장하여 API 응답 시 활용)
     * @throws DuplicateEmailException 이메일이 이미 존재하는 경우
     * @throws DuplicateUsernameException 사용자명이 이미 존재하는 경우
     */
    @NotNull
    SignupSession signup(@NotNull UserSignupRequest request) throws DuplicateEmailException, DuplicateUsernameException;

    /**
     * 인증코드를 통해 가입정보 인증을 완료하면 사용자 계정을 생성 및 저장한다.
     *
     * @param sessionKey 인증 세션 키
     * @param verificationCode 이메일 인증 코드
     * @return 생성된 유저 정보
     */
    @NotNull
    User signupComplete(@NotBlank String sessionKey, @NotBlank String verificationCode);
}
