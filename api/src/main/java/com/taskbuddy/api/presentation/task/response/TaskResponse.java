package com.taskbuddy.api.presentation.task.response;

import com.taskbuddy.api.business.task.dto.TimeFrame;
import com.taskbuddy.persistence.entity.TaskEntity;

public record TaskResponse (
        Long id,
        String title,
        String description,
        Boolean isDone,
        TimeFrame timeFrame
) {
    public static TaskResponse from(final TaskEntity task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getIsDone(),
                new TimeFrame(
                        task.getStartDateTime(),
                        task.getEndDateTime())
        );
    }
}
