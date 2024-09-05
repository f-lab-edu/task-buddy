package com.taskbuddy.api.controller.response.task;

import com.taskbuddy.core.domain.Task;

public record TaskResponse (
        Long id,
        String title,
        String description,
        Boolean isDone,
        TimeFrame timeFrame
) {
    public static TaskResponse from(final Task task) {
        return new TaskResponse(
                task.id(),
                task.title(),
                task.description(),
                task.isDone(),
                new TimeFrame(
                        task.timeFrame().startDateTime(),
                        task.timeFrame().endDateTime())
        );
    }
}
