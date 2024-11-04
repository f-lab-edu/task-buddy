package com.taskbuddy.api.business.taskreminder;

import com.taskbuddy.api.business.taskreminder.dto.TaskReminderInitialize;
import com.taskbuddy.api.business.taskreminder.dto.TaskReminderUpdate;
import com.taskbuddy.api.persistence.repository.TaskReminderJpaRepository;
import com.taskbuddy.persistence.entity.TaskReminderEntity;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Builder
@RequiredArgsConstructor
@Service
public class TaskReminderService {
    private final TaskReminderJpaRepository taskReminderJpaRepository;

    public void initialize(TaskReminderInitialize taskReminderInitialize, LocalDateTime requestDateTime) {
        final TaskReminderEntity entity = taskReminderInitialize.createEntity(requestDateTime);

        taskReminderJpaRepository.save(entity);
    }

    public void update(TaskReminderUpdate taskReminderUpdate, LocalDateTime requestDateTime) {
        Optional<TaskReminderEntity> findEntityOptional = taskReminderJpaRepository.findByTaskId(taskReminderUpdate.taskId());

        if (findEntityOptional.isEmpty()) {
            initialize(new TaskReminderInitialize(taskReminderUpdate.taskId(), taskReminderUpdate.reminderInterval()), requestDateTime);
            return;
        }

        final TaskReminderEntity updatedEntity = taskReminderUpdate.updatedEntity(findEntityOptional.get(), requestDateTime);
        taskReminderJpaRepository.save(updatedEntity);
    }

    public void deleteByTaskId(Long taskId) {
        taskReminderJpaRepository.findByTaskId(taskId)
                .ifPresent(taskReminder -> taskReminderJpaRepository.deleteById(taskReminder.getId()));
    }

}
