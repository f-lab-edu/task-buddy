package com.taskbuddy.core.domain;

import java.time.LocalDateTime;

public record TaskUpdate(
        Long id,
        Long userId,
        String title,
        String description,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {}
