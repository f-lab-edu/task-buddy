package com.taskbuddy.api.business.task;

import com.taskbuddy.api.business.taskreminder.TaskReminderService;
import com.taskbuddy.api.business.task.dto.TaskContentUpdate;
import com.taskbuddy.api.business.task.dto.TaskCreate;
import com.taskbuddy.api.business.task.dto.TaskDoneUpdate;
import com.taskbuddy.api.business.task.dto.TimeFrame;
import com.taskbuddy.api.business.taskreminder.dto.TaskReminderInitialize;
import com.taskbuddy.api.business.taskreminder.dto.TaskReminderUpdate;
import com.taskbuddy.api.error.ApplicationException;
import com.taskbuddy.api.persistence.repository.TaskJpaRepository;
import com.taskbuddy.api.presentation.ResultCodes;
import com.taskbuddy.api.presentation.task.response.TaskResponse;
import com.taskbuddy.persistence.entity.TaskEntity;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Builder
@RequiredArgsConstructor
@Service
public class TaskService {
    private final TaskJpaRepository taskJpaRepository;
    private final TaskReminderService taskReminderService;

    public TaskResponse getTaskResponse(Long id) {
        final TaskEntity task = taskJpaRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ResultCodes.T1001, "The given task with id does not exist."));

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getIsDone(),
                new TimeFrame(task.getStartDateTime(), task.getEndDateTime())
        );
    }

    @Transactional
    public Long createTask(TaskCreate taskCreate, LocalDateTime requestDateTime) {
        TaskEntity entity = taskCreate.createEntity(requestDateTime);
        final TaskEntity savedEntity = taskJpaRepository.save(entity);

        TaskReminderInitialize taskReminderInitialize = new TaskReminderInitialize(savedEntity.getId(), taskCreate.reminderInterval());
        taskReminderService.initialize(taskReminderInitialize, requestDateTime);

        return savedEntity.getId();
    }

    @Transactional
    public void updateContent(TaskContentUpdate taskContentUpdate, LocalDateTime requestDateTime) {
        TaskEntity findEntity = taskJpaRepository.findById(taskContentUpdate.id())
                .orElseThrow(() -> new ApplicationException(ResultCodes.T1001, "The given task with id does not exist."));

        TaskEntity updatedEntity = taskContentUpdate.updatedEntity(findEntity, requestDateTime);
        taskJpaRepository.save(updatedEntity);

        TaskReminderUpdate taskReminderUpdate = new TaskReminderUpdate(taskContentUpdate.id(), taskContentUpdate.reminderInterval());
        taskReminderService.update(taskReminderUpdate, requestDateTime);
    }

    public void updateDone(TaskDoneUpdate taskDoneUpdate, LocalDateTime requestDateTime) {
        final TaskEntity task = taskJpaRepository.findById(taskDoneUpdate.id())
                .orElseThrow(() -> new ApplicationException(ResultCodes.T1001, "The given task with id does not exist."));

        if (taskDoneUpdate.isDone() == task.getIsDone()) {
            return;
        }

        TaskEntity updatedEntity = taskDoneUpdate.updatedEntity(task, requestDateTime);
        taskJpaRepository.save(updatedEntity);
    }

    @Transactional
    public void deleteTask(Long id) {
        final boolean exists = taskJpaRepository.existsById(id);

        if (!exists) {
            throw new ApplicationException(ResultCodes.T1001, "The given task with id does not exist.");
        }

        taskJpaRepository.deleteById(id);
        taskReminderService.deleteByTaskId(id);
    }
}
