package com.taskbuddy.api.controller.request;

import com.taskbuddy.api.controller.response.task.TimeFrame;

public record TaskCreateRequest(
        String title,
        String description,
        TimeFrame timeFrame
) {}
