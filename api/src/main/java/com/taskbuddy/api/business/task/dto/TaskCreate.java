package com.taskbuddy.api.business.task.dto;

import java.time.Duration;
import java.time.LocalDateTime;

public record TaskCreate (
        Long userId,
        String title,
        String description,
        Boolean reminderEnabled,
        Duration reminderInterval,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {}
