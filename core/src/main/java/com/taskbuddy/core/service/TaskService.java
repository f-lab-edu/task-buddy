package com.taskbuddy.core.service;

import com.taskbuddy.core.domain.Task;
import com.taskbuddy.core.domain.TaskCreate;
import com.taskbuddy.core.domain.TaskContentUpdate;
import com.taskbuddy.core.domain.TaskDoneUpdate;
import com.taskbuddy.core.service.port.ClockHolder;
import com.taskbuddy.core.service.port.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskReminderService taskReminderService;
    private final ClockHolder clockHolder;

    public Task getTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("The given task with id does not exist."));
    }

    @Transactional
    public Long createTask(TaskCreate taskCreate) {
        Task task = Task.from(taskCreate, clockHolder);
        task = taskRepository.save(task);

        taskReminderService.initialize(task, taskCreate.reminderInterval());

        return task.getId();
    }

    @Transactional
    public void updateContent(TaskContentUpdate taskContentUpdate) {
        Task task = taskRepository.findById(taskContentUpdate.id())
                .orElseThrow(() -> new IllegalArgumentException("The given task with id does not exist."));

        task.update(taskContentUpdate, clockHolder);
        taskRepository.save(task);

        taskReminderService.update(task, taskContentUpdate.reminderInterval());
    }


    public void updateDone(TaskDoneUpdate taskDoneUpdate) {
        Task task = taskRepository.findById(taskDoneUpdate.id())
                .orElseThrow(() -> new IllegalArgumentException("The given task with id does not exist."));

        task.done(taskDoneUpdate.isDone(), clockHolder);
        taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long id) {
        final boolean exists = taskRepository.existsById(id);

        if (!exists) {
            throw new IllegalArgumentException("The given task with id does not exist.");
        }

        taskRepository.deleteById(id);
        taskReminderService.deleteByTaskId(id);
    }
}
