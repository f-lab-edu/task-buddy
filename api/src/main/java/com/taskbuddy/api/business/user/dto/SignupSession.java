package com.taskbuddy.api.business.user.dto;

public record SignupSession(String key) {

    public SignupSession {
        assert key != null && key.matches("^[A-Za-z0-9!@#$%^&*()]{50}$") : "키는 랜덤한 문자 50자리로 구성되어야 한다.";
    }
}
