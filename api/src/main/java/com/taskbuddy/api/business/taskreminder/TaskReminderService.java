package com.taskbuddy.api.business.taskreminder;

import com.taskbuddy.api.business.taskreminder.dto.TaskReminderInitialize;
import com.taskbuddy.api.business.taskreminder.dto.TaskReminderUpdate;
import com.taskbuddy.api.persistence.repository.TaskReminderJpaRepository;
import com.taskbuddy.persistence.entity.TaskEntity;
import com.taskbuddy.persistence.entity.TaskReminderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TaskReminderService {
    private final TaskReminderJpaRepository taskReminderJpaRepository;

    public void initialize(TaskReminderInitialize taskReminderInitialize, LocalDateTime requestDateTime) {
        final TaskReminderEntity entity = savedEntity(taskReminderInitialize, requestDateTime);

        taskReminderJpaRepository.save(entity);
    }

    private TaskReminderEntity savedEntity(TaskReminderInitialize taskReminderInitialize, LocalDateTime requestDateTime) {
        return TaskReminderEntity.builder()
                .task(TaskEntity.builder()
                        .id(taskReminderInitialize.taskId())
                        .build())
                .reminderInterval(taskReminderInitialize.reminderInterval())
                .createdAt(requestDateTime)
                .updatedAt(requestDateTime)
                .build();
    }

    public void update(TaskReminderUpdate taskReminderUpdate, LocalDateTime requestDateTime) {
        Optional<TaskReminderEntity> optionalTaskReminder = taskReminderJpaRepository.findByTaskId(taskReminderUpdate.taskId());

        if (optionalTaskReminder.isEmpty()) {
            initialize(new TaskReminderInitialize(taskReminderUpdate.taskId(), taskReminderUpdate.reminderInterval()), requestDateTime);
            return;
        }

        final TaskReminderEntity taskReminder = updateReminderInterval(optionalTaskReminder.get(), taskReminderUpdate, requestDateTime);
        taskReminderJpaRepository.save(taskReminder);
    }

    private TaskReminderEntity updateReminderInterval(TaskReminderEntity taskReminder, TaskReminderUpdate taskReminderUpdate, LocalDateTime requestDateTime) {
        return taskReminder.builderOfCopy()
                .reminderInterval(taskReminderUpdate.reminderInterval())
                .updatedAt(requestDateTime)
                .build();
    }

    public void deleteByTaskId(Long taskId) {
        taskReminderJpaRepository.findByTaskId(taskId)
                .ifPresent(taskReminder -> taskReminderJpaRepository.deleteById(taskReminder.getId()));
    }

}
