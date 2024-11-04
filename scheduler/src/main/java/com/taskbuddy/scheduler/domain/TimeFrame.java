package com.taskbuddy.scheduler.domain;

import org.springframework.util.Assert;

import java.time.LocalDateTime;

public record TimeFrame(
        LocalDateTime startDateTime,
        LocalDateTime endDateTime) {
    public TimeFrame {
        Assert.notNull(startDateTime, "The startDateTime must not be null.");
        Assert.notNull(endDateTime, "The endDateTime must not be null.");
        Assert.isTrue(endDateTime.isAfter(startDateTime), "The endDateTime must be after than the startDateTime.");
    }
}
