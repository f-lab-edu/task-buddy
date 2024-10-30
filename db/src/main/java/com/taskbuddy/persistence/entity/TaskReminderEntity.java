package com.taskbuddy.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
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

    public TaskReminderEntity.TaskReminderEntityBuilder builderOfCopy() {
        return TaskReminderEntity.builder()
                .id(id)
                .task(task)
                .lastReminderSentTime(lastReminderSentTime)
                .reminderInterval(reminderInterval)
                .createdAt(createdAt)
                .updatedAt(updatedAt);
    }

    public Long getTaskId() {
        return task.getId();
    }
}
