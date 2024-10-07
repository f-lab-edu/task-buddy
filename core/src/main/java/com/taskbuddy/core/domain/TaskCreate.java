package com.taskbuddy.core.domain;

import java.time.LocalDateTime;

public record TaskCreate (
        Long userId,
        String title,
        String description,
        Boolean reminderEnabled,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {}
