package com.taskbuddy.consumer.notification.service;

import com.taskbuddy.consumer.notification.service.dto.PushSendResponse;
import com.taskbuddy.queue.message.TaskReminderMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class NotificationService {

    public PushSendResponse sendPush(TaskReminderMessage message) {
        // Push Interface

        // FIXME 임시 데이터 반환 및 푸시 발송 구현하기
        return new PushSendResponse(message.getId(), true, LocalDateTime.now());
    }
}
