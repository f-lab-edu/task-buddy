package com.taskbuddy.api.business.task.dto;

public record TaskDoneUpdate(
        Long id,
        Long userId,
        Boolean isDone
) {}
