package com.taskbuddy.api.business.user;

import com.taskbuddy.api.business.user.dto.SignupSession;
import com.taskbuddy.api.error.exception.DuplicateEmailException;
import com.taskbuddy.api.error.exception.DuplicateUsernameException;
import com.taskbuddy.api.presentation.user.request.UserSignupRequest;
import jakarta.validation.constraints.NotNull;

public interface SignupService {

    /**
     * 사용자의 가입을 처리한다.
     *
     * <p>이 메서드는 사용자 가입 요청을 받아 다음 과정을 수행한다.:
     * <ol>
     *     <li>이메일과 사용자명(username)의 중복 여부를 검증한다.</li>
     *     <li>검증이 완료되면, 이메일로 인증 코드를 발송한다.</li>
     *     <li>발송된 인증 코드와 이메일 정보를 캐시 저장소에 저장한다.</li>
     *     <li>해당 정보를 기반으로 세션 키를 생성하여 반환한다.</li>
     * </ol>
     * </p>
     *
     * @param request 유저 가입 요청 정보 (이메일, 사용자명, 비밀번호 포함)
     * @return {@link SignupSession} 생성된 인증 세션 키 (쿠키에 저장하여 API 응답 시 활용)
     * @throws DuplicateEmailException 이메일이 이미 존재하는 경우
     * @throws DuplicateUsernameException 사용자명이 이미 존재하는 경우
     */
    @NotNull
    SignupSession signup(@NotNull UserSignupRequest request) throws DuplicateEmailException, DuplicateUsernameException;
}
