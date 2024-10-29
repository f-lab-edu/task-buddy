package com.taskbuddy.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
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

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    private TaskEntity(Long id, String title, Boolean isDone, String description, LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.isDone = isDone;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}