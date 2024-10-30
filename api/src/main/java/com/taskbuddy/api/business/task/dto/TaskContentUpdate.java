package com.taskbuddy.api.business.task.dto;

import com.taskbuddy.persistence.entity.TaskEntity;

import java.time.Duration;
import java.time.LocalDateTime;

public record TaskContentUpdate(
        Long id,
        Long userId,
        String title,
        String description,
        Boolean reminderEnabled,
        Duration reminderInterval,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {
    public TaskEntity updatedEntity(TaskEntity entity, LocalDateTime requestDateTime) {
        return entity.builderOfCopy()
                .title(title)
                .description(description)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .updatedAt(requestDateTime)
                .build();
    }
}
