package com.taskbuddy.core.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class User {
    private Long id;
    private boolean loggedIn;
    private boolean reminderEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
