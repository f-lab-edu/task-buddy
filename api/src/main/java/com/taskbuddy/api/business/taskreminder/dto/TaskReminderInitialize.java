package com.taskbuddy.api.business.taskreminder.dto;

import com.taskbuddy.persistence.entity.TaskEntity;
import com.taskbuddy.persistence.entity.TaskReminderEntity;

import java.time.Duration;
import java.time.LocalDateTime;

public record TaskReminderInitialize(
        Long taskId,
        Duration reminderInterval
) {
    public TaskReminderEntity createEntity(LocalDateTime requestDateTime) {
        return TaskReminderEntity.builder()
                .task(TaskEntity.builder()
                        .id(taskId)
                        .build())
                .reminderInterval(reminderInterval)
                .createdAt(requestDateTime)
                .updatedAt(requestDateTime)
                .build();
    }
}
