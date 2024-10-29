package com.taskbuddy.api.domain;

public record TaskDoneUpdate(
        Long id,
        Long userId,
        Boolean isDone
) {}
