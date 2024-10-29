package com.taskbuddy.api.controller.request;

import com.taskbuddy.api.domain.TimeFrame;
import org.springframework.util.Assert;

public record TaskContentUpdateRequest(
        String title,
        String description,
        TimeFrame timeFrame
) {
    public TaskContentUpdateRequest {
        Assert.state(title != null && !title.isBlank(), "The title of task must not be blank.");
        Assert.state(description == null || description.length() <= 500, "The description length must be equal or less than 500");
        Assert.notNull(timeFrame, "The timeFrame must not be null.");
    }
}
