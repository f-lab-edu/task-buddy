package com.taskbuddy.core.domain;

import java.time.Duration;
import java.time.LocalDateTime;

public record TaskContentUpdate(
        Long id,
        Long userId,
        String title,
        String description,
        Boolean reminderEnabled,
        Duration reminderInterval,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {}
