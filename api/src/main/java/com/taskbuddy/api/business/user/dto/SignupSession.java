package com.taskbuddy.api.business.user.dto;

public record SignupSession(String key) {

    public SignupSession {
        assert key != null && key.matches("\\d{6}") : "키는 숫자 6자리로 구성되어야 한다.";
    }
}
