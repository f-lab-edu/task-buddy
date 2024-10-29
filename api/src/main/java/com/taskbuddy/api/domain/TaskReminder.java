package com.taskbuddy.api.domain;

import com.taskbuddy.api.business.port.ClockHolder;
import com.taskbuddy.persistence.entity.TaskReminderEntity;
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

    public static TaskReminder from(Task task, Duration reminderInterval, ClockHolder clockHolder) {
        final LocalDateTime currentDateTime = clockHolder.currentDateTime();

        return TaskReminder.builder()
                .task(task)
                .reminderInterval(reminderInterval)
                .createdAt(currentDateTime)
                .updatedAt(currentDateTime)
                .build();
    }

    public static TaskReminder from(final TaskReminderEntity entity) {
        return TaskReminder.builder()
                .id(entity.getId())
                .task(Task.from(entity.getTask()))
                .lastReminderSentTime(entity.getLastReminderSentTime())
                .reminderInterval(entity.getReminderInterval())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateReminderInterval(Duration reminderInterval, ClockHolder clockHolder) {
        this.reminderInterval = reminderInterval;
        this.updatedAt = clockHolder.currentDateTime();
    }

    public TaskReminderEntity toEntity() {
        return TaskReminderEntity.builder()
                .id(id)
                .task(task.toEntity())
                .lastReminderSentTime(lastReminderSentTime)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
