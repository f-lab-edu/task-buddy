package com.taskbuddy.core.domain;

import com.taskbuddy.core.service.port.ClockHolder;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
public class ReminderSettings {
    private final Long id;
    private final Task task;
    private LocalDateTime lastReminderSentTime;
    private Duration reminderInterval;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public ReminderSettings(Long id, Task task, LocalDateTime lastReminderSentTime, Duration reminderInterval, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.task = task;
        this.lastReminderSentTime = lastReminderSentTime;
        this.reminderInterval = reminderInterval;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ReminderSettings from(Task task, Duration reminderInterval, ClockHolder clockHolder) {
        final LocalDateTime currentDateTime = clockHolder.currentDateTime();

        return ReminderSettings.builder()
                .task(task)
                .reminderInterval(reminderInterval)
                .createdAt(currentDateTime)
                .updatedAt(currentDateTime)
                .build();
    }

    public void updateLastReminderSentTime(LocalDateTime lastReminderSentTime, ClockHolder clockHolder) {
        this.lastReminderSentTime = lastReminderSentTime;
        this.updatedAt = clockHolder.currentDateTime();
    }

    public boolean isReminderDue(ClockHolder clockHolder) {
        LocalDateTime currentDateTime = clockHolder.currentDateTime();
        LocalDateTime taskStartDateTime = task.getTimeFrame().startDateTime();

        if (currentDateTime.isBefore(taskStartDateTime)) {
            return false;
        }

        Duration timeSinceStart = Duration.between(taskStartDateTime, currentDateTime);

        return timeSinceStart.toMinutes() % reminderInterval.toMinutes() == 0;
    }

    public void updateReminderInterval(Duration reminderInterval, ClockHolder clockHolder) {
        this.reminderInterval = reminderInterval;
        this.updatedAt = clockHolder.currentDateTime();
    }

    public Long getTaskId() {
        return task.getId();
    }
}
