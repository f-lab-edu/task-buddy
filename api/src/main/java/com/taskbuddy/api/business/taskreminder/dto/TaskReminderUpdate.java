package com.taskbuddy.api.business.taskreminder.dto;

import com.taskbuddy.persistence.entity.TaskReminderEntity;

import java.time.Duration;
import java.time.LocalDateTime;

public record TaskReminderUpdate(
        Long taskId,
        Duration reminderInterval
) {
    public TaskReminderEntity updatedEntity(TaskReminderEntity entity, LocalDateTime requestDateTime) {
        return entity.builderOfCopy()
                .reminderInterval(reminderInterval)
                .updatedAt(requestDateTime)
                .build();
    }
}
