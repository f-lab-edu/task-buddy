package com.taskbuddy.core.domain;

import java.time.LocalDateTime;

public record Task(
        Long id,
        String title,
        String description,
        Boolean isDone,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {
}
