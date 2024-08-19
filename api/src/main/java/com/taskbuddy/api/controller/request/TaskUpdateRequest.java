package com.taskbuddy.api.controller.request;

import com.taskbuddy.api.controller.response.task.TimeFrame;

public record TaskUpdateRequest(
        String title,
        String description,
        TimeFrame timeFrame
) {}
