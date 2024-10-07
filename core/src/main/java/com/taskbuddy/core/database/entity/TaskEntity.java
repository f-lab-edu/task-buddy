package com.taskbuddy.core.database.entity;

import com.taskbuddy.core.domain.Task;
import com.taskbuddy.core.domain.TimeFrame;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tasks")
@Entity
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Boolean isDone;

    private String description;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private Boolean isReminderEnabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    private TaskEntity(Long id, String title, Boolean isDone, String description, LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean isReminderEnabled, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.isDone = isDone;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.isReminderEnabled = isReminderEnabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TaskEntity from(Task task) {
        return TaskEntity.builder()
                .id(task.getId())
                .title(task.getTitle())
                .isDone(task.getIsDone())
                .description(task.getDescription())
                .startDateTime(task.getTimeFrame().startDateTime())
                .endDateTime(task.getTimeFrame().endDateTime())
                .isReminderEnabled(task.isReminderEnabled())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    public Task toModel() {
        return Task.builder()
                .id(id)
                .title(title)
                .isDone(isDone)
                .description(description)
                .timeFrame(new TimeFrame(startDateTime, endDateTime))
                .reminderEnabled(isReminderEnabled)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
