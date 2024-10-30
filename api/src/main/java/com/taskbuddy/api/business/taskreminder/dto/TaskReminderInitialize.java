package com.taskbuddy.api.business.taskreminder.dto;

import java.time.Duration;

public record TaskReminderInitialize(
        Long taskId,
        Duration reminderInterval
) {
}
