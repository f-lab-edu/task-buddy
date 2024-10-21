package com.taskbuddy.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TaskReminderScheduler {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void execute() {
//        ResponseEntity<List<String>> response = taskReminderController.getAllToSendReminder();
//        if (!response.getStatusCode().is2xxSuccessful()) {
//            throw new RuntimeException();
//        }
//
//        final List<String> allToSendReminder = response.getBody();
//
//        if (CollectionUtils.isEmpty(allToSendReminder)) {
//            return;
//        }
//
//        for (String reminder : allToSendReminder) {
//            kafkaTemplate.send("task-reminders", JsonUtils.serialize(reminder));
//        }
    }
}
