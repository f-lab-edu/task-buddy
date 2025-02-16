package com.taskbuddy.api.business.user;

public record UserCreate(String email,
                         String username,
                         String password) {
}
