package com.taskbuddy.core.domain;

public record Task(
        Long id,
        String title,
        String description,
        Boolean isDone,
        TimeFrame timeFrame
) {
    public static Task from(TaskCreate taskCreate) {
        return null;
    }
}
