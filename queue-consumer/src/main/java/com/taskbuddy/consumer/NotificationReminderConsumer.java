package com.taskbuddy.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationReminderConsumer {

    @KafkaListener(topics = "task-reminders", groupId = "reminder-group")
    public void listen(String message) {
        log.info("Received message: " + message);
    }
}
