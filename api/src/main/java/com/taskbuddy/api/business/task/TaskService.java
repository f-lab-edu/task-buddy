package com.taskbuddy.api.business.task;

import com.taskbuddy.api.business.taskreminder.TaskReminderService;
import com.taskbuddy.api.business.dto.*;
import com.taskbuddy.api.business.task.dto.TaskContentUpdate;
import com.taskbuddy.api.business.task.dto.TaskCreate;
import com.taskbuddy.api.business.task.dto.TaskDoneUpdate;
import com.taskbuddy.api.business.task.dto.TimeFrame;
import com.taskbuddy.api.business.taskreminder.dto.TaskReminderInitialize;
import com.taskbuddy.api.business.taskreminder.dto.TaskReminderUpdate;
import com.taskbuddy.api.persistence.repository.TaskJpaRepository;
import com.taskbuddy.api.presentation.task.response.TaskResponse;
import com.taskbuddy.persistence.entity.TaskEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class TaskService {
    private final TaskJpaRepository taskJpaRepository;
    private final TaskReminderService taskReminderService;

    public TaskResponse getTaskResponse(Long id) {
        final TaskEntity task = taskJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("The given task with id does not exist."));


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
        final TaskEntity savedTask = taskJpaRepository.save(createEntity(taskCreate, requestDateTime));

        TaskReminderInitialize taskReminderInitialize = new TaskReminderInitialize(savedTask.getId(), taskCreate.reminderInterval());
        taskReminderService.initialize(taskReminderInitialize, requestDateTime);

        return savedTask.getId();
    }

    private TaskEntity createEntity(TaskCreate taskCreate, LocalDateTime requestDateTime) {
        final boolean isDoneDefaultValue = false;

        return TaskEntity.builder()
                .title(taskCreate.title())
                .isDone(isDoneDefaultValue)
                .description(taskCreate.description())
                .startDateTime(taskCreate.startDateTime())
                .endDateTime(taskCreate.endDateTime())
                .createdAt(requestDateTime)
                .updatedAt(requestDateTime)
                .build();
    }

    @Transactional
    public void updateContent(TaskContentUpdate taskContentUpdate, LocalDateTime requestDateTime) {
        TaskEntity findEntity = taskJpaRepository.findById(taskContentUpdate.id())
                .orElseThrow(() -> new IllegalArgumentException("The given task with id does not exist."));

        TaskEntity updatedEntity = updateContent(findEntity, taskContentUpdate, requestDateTime);
        taskJpaRepository.save(updatedEntity);

        taskReminderService.update(new TaskReminderUpdate(taskContentUpdate.id(), taskContentUpdate.reminderInterval()), requestDateTime);
    }

    private TaskEntity updateContent(TaskEntity task, TaskContentUpdate taskContentUpdate, LocalDateTime requestDateTime) {
        return task.builderOfCopy()
                .title(taskContentUpdate.title())
                .description(taskContentUpdate.description())
                .startDateTime(taskContentUpdate.startDateTime())
                .endDateTime(taskContentUpdate.endDateTime())
                .updatedAt(requestDateTime)
                .build();
    }

    public void updateDone(TaskDoneUpdate taskDoneUpdate, LocalDateTime requestDateTime) {
        final TaskEntity task = taskJpaRepository.findById(taskDoneUpdate.id())
                .orElseThrow(() -> new IllegalArgumentException("The given task with id does not exist."));

        if (taskDoneUpdate.isDone() == task.getIsDone()) {
            return;
        }

        taskJpaRepository.save(updateDone(task, taskDoneUpdate, requestDateTime));
    }

    private TaskEntity updateDone(TaskEntity task, TaskDoneUpdate taskDoneUpdate, LocalDateTime requestDateTime) {
        return task.builderOfCopy()
                .isDone(taskDoneUpdate.isDone())
                .updatedAt(requestDateTime)
                .build();
    }

    @Transactional
    public void deleteTask(Long id) {
        final boolean exists = taskJpaRepository.existsById(id);

        if (!exists) {
            throw new IllegalArgumentException("The given task with id does not exist.");
        }

        taskJpaRepository.deleteById(id);
        taskReminderService.deleteByTaskId(id);
    }
}
