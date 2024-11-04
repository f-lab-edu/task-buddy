package com.taskbuddy.scheduler.domain;

import com.taskbuddy.scheduler.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Task {
    private final Long id;
    private final User user;
    private String title;
    private Boolean isDone;
    private String description;
    private TimeFrame timeFrame;
    private boolean reminderEnabled;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public Task(Long id, User user, String title, Boolean isDone, String description, TimeFrame timeFrame, boolean reminderEnabled, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.isDone = isDone;
        this.description = description;
        this.timeFrame = timeFrame;
        this.reminderEnabled = reminderEnabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getUserId() {
        return user.getId();
    }
}
