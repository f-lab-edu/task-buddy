package com.taskbuddy.queue.topic;

public interface Topics {
    interface TaskReminder {
        String PUSH = "task-reminder-push-topic";
        String DB_UPDATE = "task-reminder-db-update-topic";
    }
}
