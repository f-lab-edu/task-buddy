package com.taskbuddy.consumer.notification.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TaskReminderService {

    public void updateLastSentTime(LocalDateTime sentDateTime) {
        // TODO core에서 업데이트로직 옮겨오기
    }
}
