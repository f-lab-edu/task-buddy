package com.taskbuddy.consumer.notification;

import com.taskbuddy.consumer.notification.service.NotificationService;
import com.taskbuddy.consumer.notification.service.TaskReminderService;
import com.taskbuddy.consumer.notification.service.dto.PushSendResponse;
import com.taskbuddy.queue.group.GroupIds;
import com.taskbuddy.queue.message.TaskReminderMessage;
import com.taskbuddy.queue.topic.Topics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationReminderListener {
    private final NotificationService notificationService;
    private final TaskReminderService taskReminderService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = Topics.TaskReminder.PUSH, groupId = GroupIds.TASK_REMINDER)
    public void listenForSendPush(TaskReminderMessage message) {
        PushSendResponse response = notificationService.sendPush(message);

        kafkaTemplate.send(Topics.TaskReminder.DB_UPDATE, response);
    }

    @KafkaListener(topics = Topics.TaskReminder.DB_UPDATE, groupId = GroupIds.TASK_REMINDER)
    public void listenForDbUpdate(PushSendResponse message) {
        if (message.isSentSuccessfully()) {
            taskReminderService.updateLastSentTime(message.getSentDateTime());
        }
    }
}
