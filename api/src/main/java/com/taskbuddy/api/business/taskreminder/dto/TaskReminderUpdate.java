package com.taskbuddy.api.business.taskreminder.dto;

import java.time.Duration;

public record TaskReminderUpdate(
        Long taskId,
        Duration reminderInterval
) {
}
