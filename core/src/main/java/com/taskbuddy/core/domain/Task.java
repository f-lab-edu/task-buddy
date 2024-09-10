package com.taskbuddy.core.domain;

import com.taskbuddy.core.service.ClockHolder;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Task {
    private final Long id;
//    private User user;
    private String title;
    private Boolean isDone;
    private String description;
    private TimeFrame timeFrame;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public Task(Long id, String title, String description, Boolean isDone, TimeFrame timeFrame, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.isDone = isDone;
        this.timeFrame = timeFrame;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Task from(TaskCreate taskCreate, ClockHolder clockHolder) {
        final LocalDateTime createdAt = clockHolder.currentDateTime();
        final boolean isDoneDefaultValue = false;

        return Task.builder()
                .title(taskCreate.title())
                .isDone(isDoneDefaultValue)
                .description(taskCreate.description())
                .timeFrame(new TimeFrame(taskCreate.startDateTime(), taskCreate.endDateTime()))
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();
    }

    public void update(TaskContentUpdate taskContentUpdate, ClockHolder clockHolder) {
        title = taskContentUpdate.title();
        description = taskContentUpdate.description();
        timeFrame = new TimeFrame(taskContentUpdate.startDateTime(), taskContentUpdate.endDateTime());
        updatedAt = clockHolder.currentDateTime();
    }
}
