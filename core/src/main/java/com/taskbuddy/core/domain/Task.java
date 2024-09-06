package com.taskbuddy.core.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Task {
    private Long id;
//    private User user;
    private String title;
    private String description;
    private Boolean isDone;
    private TimeFrame timeFrame;

    @Builder
    public Task(Long id, String title, String description, Boolean isDone, TimeFrame timeFrame) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.isDone = isDone;
        this.timeFrame = timeFrame;
    }

    public static Task from(TaskCreate taskCreate) {
        return Task.builder()
                .title(taskCreate.title())
                .isDone(taskCreate.isDone())
                .description(taskCreate.description())
                .timeFrame(new TimeFrame(taskCreate.startDateTime(), taskCreate.endDateTime()))
                .build();
    }
}
