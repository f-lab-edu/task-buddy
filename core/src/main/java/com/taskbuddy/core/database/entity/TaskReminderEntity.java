package com.taskbuddy.core.database.entity;

import com.taskbuddy.core.domain.TaskReminder;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "task_reminders")
@Entity
public class TaskReminderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "task_id")
    private TaskEntity task;

    private LocalDateTime lastReminderSentTime;

    private Duration reminderInterval;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    private TaskReminderEntity(Long id, TaskEntity task, LocalDateTime lastReminderSentTime, Duration reminderInterval, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.task = task;
        this.lastReminderSentTime = lastReminderSentTime;
        this.reminderInterval = reminderInterval;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TaskReminderEntity from(TaskReminder taskReminder) {
        return TaskReminderEntity.builder()
                .id(taskReminder.getId())
                .task(TaskEntity.from(taskReminder.getTask()))
                .lastReminderSentTime(taskReminder.getLastReminderSentTime())
                .createdAt(taskReminder.getCreatedAt())
                .updatedAt(taskReminder.getUpdatedAt())
                .build();
    }

    public TaskReminder toModel() {
        return TaskReminder.builder()
                .id(id)
                .task(task.toModel())
                .lastReminderSentTime(lastReminderSentTime)
                .reminderInterval(reminderInterval)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public Long getTaskId() {
        return task.getId();
    }
}
