package com.taskbuddy.api.domain;

import com.taskbuddy.api.business.port.ClockHolder;
import com.taskbuddy.api.domain.user.User;
import com.taskbuddy.persistence.entity.TaskEntity;
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

    public static Task from(final TaskCreate taskCreate, ClockHolder clockHolder) {
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

    public static Task from(final TaskEntity entity) {
        return Task.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .isDone(entity.getIsDone())
                .description(entity.getDescription())
                .timeFrame(new TimeFrame(entity.getStartDateTime(), entity.getEndDateTime()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public TaskEntity toEntity() {
        return TaskEntity.builder()
                .id(id)
                .title(title)
                .isDone(isDone)
                .description(description)
                .startDateTime(timeFrame.startDateTime())
                .endDateTime(timeFrame.endDateTime())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
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
}
