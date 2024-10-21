package com.taskbuddy.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationReminderConsumer {

    @KafkaListener(topics = "task-reminders", groupId = "reminder-group")
    public void listen(String message) {
        System.out.println("Received message: " + message);
    }
}
