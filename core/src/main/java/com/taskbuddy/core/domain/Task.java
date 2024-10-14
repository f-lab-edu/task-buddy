package com.taskbuddy.core.domain;

import com.taskbuddy.core.domain.user.User;
import com.taskbuddy.core.service.port.ClockHolder;
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

    public static Task from(TaskCreate taskCreate, ClockHolder clockHolder) {
        final LocalDateTime createdAt = clockHolder.currentDateTime();
        final boolean isDoneDefaultValue = false;

        return Task.builder()
                .title(taskCreate.title())
                .isDone(isDoneDefaultValue)
                .description(taskCreate.description())
                .timeFrame(new TimeFrame(taskCreate.startDateTime(), taskCreate.endDateTime()))
                .reminderEnabled(taskCreate.reminderEnabled())
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();
    }

    public void update(TaskContentUpdate taskContentUpdate, ClockHolder clockHolder) {
        title = taskContentUpdate.title();
        description = taskContentUpdate.description();
        timeFrame = new TimeFrame(taskContentUpdate.startDateTime(), taskContentUpdate.endDateTime());
        reminderEnabled = taskContentUpdate.reminderEnabled();
        updatedAt = clockHolder.currentDateTime();
    }

    public void done(boolean isDone, ClockHolder clockHolder) {
        if (this.isDone == isDone) {
            return;
        }

        this.isDone = isDone;
        updatedAt = clockHolder.currentDateTime();
    }

    public Long getUserId() {
        return user.getId();
    }
}
