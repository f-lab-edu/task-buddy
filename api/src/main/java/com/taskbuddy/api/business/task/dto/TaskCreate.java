package com.taskbuddy.api.business.task.dto;

import com.taskbuddy.persistence.entity.TaskEntity;

import java.time.Duration;
import java.time.LocalDateTime;

public record TaskCreate (
        Long userId,
        String title,
        String description,
        Boolean reminderEnabled,
        Duration reminderInterval,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {
    public TaskEntity createEntity(final LocalDateTime requestDateTime) {
        final boolean isDoneDefaultValue = false;

        return TaskEntity.builder()
                .title(title)
                .isDone(isDoneDefaultValue)
                .description(description)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .createdAt(requestDateTime)
                .updatedAt(requestDateTime)
                .build();
    }
}
