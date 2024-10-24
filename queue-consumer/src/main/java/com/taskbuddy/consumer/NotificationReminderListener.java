package com.taskbuddy.consumer;

import com.taskbuddy.queue.message.TaskReminderMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationReminderListener {

    @KafkaListener(topics = "task-reminders", groupId = "group-notification")
    public void listen(TaskReminderMessage message) {
        log.info("Received message: " + message);
    }
}
