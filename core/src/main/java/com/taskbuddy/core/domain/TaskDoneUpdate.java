package com.taskbuddy.core.domain;

public record TaskDoneUpdate(
        Long id,
        Long userId,
        Boolean isDone
) {}
