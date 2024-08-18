package com.taskbuddy.api.controller.response.task;

public record TaskResponse (
        Long id,
        String title,
        String description,
        Boolean isDone,
        TimeFrame timeFrame
) {}
