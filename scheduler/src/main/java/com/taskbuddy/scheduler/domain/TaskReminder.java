package com.taskbuddy.scheduler.domain;

import com.taskbuddy.scheduler.business.port.ClockHolder;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
public class TaskReminder {
    private final Long id;
    private final Task task;
    private LocalDateTime lastReminderSentTime;
    private Duration reminderInterval;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public TaskReminder(Long id, Task task, LocalDateTime lastReminderSentTime, Duration reminderInterval, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.task = task;
        this.lastReminderSentTime = lastReminderSentTime;
        this.reminderInterval = reminderInterval;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public boolean isReminderDue(ClockHolder clockHolder) {
        LocalDateTime currentDateTime = clockHolder.currentDateTime();
        TimeFrame timeFrame = task.getTimeFrame();

        if (currentDateTime.isBefore(timeFrame.startDateTime()) || currentDateTime.isAfter(timeFrame.endDateTime())) {
            return false;
        }

        Duration timeSinceStart = Duration.between(timeFrame.startDateTime(), currentDateTime);

        return timeSinceStart.toMinutes() % reminderInterval.toMinutes() == 0;
    }
}
