package com.taskbuddy.consumer.notification.service;

import com.taskbuddy.consumer.notification.persistence.TaskReminderJpaRepository;
import com.taskbuddy.persistence.entity.TaskReminderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class TaskReminderService {
    private final TaskReminderJpaRepository taskReminderJpaRepository;

    @Transactional
    public void updateLastSentTime(Long id, LocalDateTime sentDateTime, LocalDateTime requestDateTime) {
        TaskReminderEntity taskReminder = taskReminderJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("The given task reminder with id does not exist."));

        taskReminderJpaRepository.save(updatedEntity(sentDateTime, requestDateTime, taskReminder));
    }

    private TaskReminderEntity updatedEntity(LocalDateTime sentDateTime, LocalDateTime requestDateTime, TaskReminderEntity taskReminder) {
        return taskReminder.builderOfCopy()
                .lastReminderSentTime(sentDateTime)
                .updatedAt(requestDateTime)
                .build();
    }
}
