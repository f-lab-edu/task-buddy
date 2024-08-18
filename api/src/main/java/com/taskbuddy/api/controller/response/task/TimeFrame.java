package com.taskbuddy.api.controller.response.task;

import java.time.LocalDateTime;

public record TimeFrame(
        LocalDateTime startDateTime,
        LocalDateTime endDateTime) {}
