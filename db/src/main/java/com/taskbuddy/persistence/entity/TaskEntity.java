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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String title;

    private Boolean isDone;

    private String description;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    private TaskEntity(Long id, UserEntity user, String title, Boolean isDone, String description, LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.isDone = isDone;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public TaskEntityBuilder builderOfCopy() {
        return TaskEntity.builder()
                .id(id)
                .user(user)
                .title(title)
                .isDone(isDone)
                .description(description)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .createdAt(createdAt)
                .updatedAt(updatedAt);
    }
}
