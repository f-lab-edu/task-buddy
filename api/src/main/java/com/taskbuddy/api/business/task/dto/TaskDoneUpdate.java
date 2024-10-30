package com.taskbuddy.api.business.task.dto;

import com.taskbuddy.persistence.entity.TaskEntity;

import java.time.LocalDateTime;

public record TaskDoneUpdate(
        Long id,
        Long userId,
        Boolean isDone
) {
    public TaskEntity updatedEntity(TaskEntity task, LocalDateTime requestDateTime) {
        return task.builderOfCopy()
                .isDone(isDone)
                .updatedAt(requestDateTime)
                .build();
    }
}
