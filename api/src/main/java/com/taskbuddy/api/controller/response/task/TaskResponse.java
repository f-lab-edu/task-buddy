package com.taskbuddy.api.controller.response.task;

import com.taskbuddy.api.domain.Task;
import com.taskbuddy.api.domain.TimeFrame;

public record TaskResponse (
        Long id,
        String title,
        String description,
        Boolean isDone,
        TimeFrame timeFrame
) {
    public static TaskResponse from(final Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getIsDone(),
                new TimeFrame(
                        task.getTimeFrame().startDateTime(),
                        task.getTimeFrame().endDateTime())
        );
    }
}
