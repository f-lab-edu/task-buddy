package com.taskbuddy.queue.message;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskReminderMessage {
    private Long id;
    private String title;

    @Builder
    public TaskReminderMessage(Long id, String title) {
        this.id = id;
        this.title = title;
    }
}
