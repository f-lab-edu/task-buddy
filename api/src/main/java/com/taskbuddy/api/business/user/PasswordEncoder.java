package com.taskbuddy.api.business.user;

public interface PasswordEncoder {

    /**
     * 평문 비밀번호를 해시하여 안전하게 저장 가능한 문자열로 변환한다.
     *
     * @param plainPassword 평문 비밀번호
     * @return 해시 처리된 비밀번호 문자열
     */
    String encode(String plainPassword);

    /**
     * 입력된 평문 비밀번호가 저장된 해시 비밀번호와 일치하는지 확인한다.
     *
     * @param plainPassword 평문 비밀번호
     * @param hashedPassword 저장된 해시 비밀번호
     * @return 비밀번호가 일치하면 {@code true}, 그렇지 않으면 {@code false}
     */
    boolean matches(String plainPassword, String hashedPassword);
}
