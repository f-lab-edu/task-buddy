package com.taskbuddy.core.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class User {
    private Long id;
    private boolean loggedIn;
    private boolean reminderEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
