package com.taskbuddy.core.domain;

import java.time.LocalDateTime;

public record TaskCreate (
        Long userId,
        String title,
        Boolean isDone,
        String description,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {}
