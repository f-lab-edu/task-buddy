package com.taskbuddy.core.domain;

public record Task(
        Long id,
        String title,
        String description,
        Boolean isDone,
        TimeFrame timeFrame
) {
}
