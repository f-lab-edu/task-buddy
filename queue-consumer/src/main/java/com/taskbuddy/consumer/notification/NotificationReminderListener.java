package com.taskbuddy.consumer.notification;

import com.taskbuddy.consumer.notification.service.NotificationService;
import com.taskbuddy.consumer.notification.service.TaskReminderService;
import com.taskbuddy.consumer.notification.service.dto.PushSendResponse;
import com.taskbuddy.queue.message.TaskReminderMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationReminderListener {
    private final NotificationService notificationService;
    private final TaskReminderService taskReminderService;

    @KafkaListener(topics = "task-reminders", groupId = "group-notification")
    public void listen(TaskReminderMessage message) {
        PushSendResponse response = notificationService.sendPush(message);

        if (response.isSentSuccessfully()) {
            taskReminderService.updateLastSentTime(response.getSentDateTime());
        }
    }
}
