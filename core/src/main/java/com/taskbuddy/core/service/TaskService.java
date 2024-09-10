package com.taskbuddy.core.service;

import com.taskbuddy.core.domain.Task;
import com.taskbuddy.core.domain.TaskCreate;
import com.taskbuddy.core.domain.TaskContentUpdate;
import com.taskbuddy.core.domain.TaskDoneUpdate;
import com.taskbuddy.core.service.port.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ClockHolder clockHolder;

    public Task getTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("The given task with id does not exist."));
    }

    public Long createTask(TaskCreate taskCreate) {
        final Task task = Task.from(taskCreate, clockHolder);
        final Task savedTask = taskRepository.save(task);

        return savedTask.getId();
    }

    public void updateContent(TaskContentUpdate taskContentUpdate) {
        Task task = taskRepository.findById(taskContentUpdate.id())
                .orElseThrow(() -> new IllegalArgumentException("The given task with id does not exist."));

        task.update(taskContentUpdate, clockHolder);
        taskRepository.save(task);
    }

    public void updateDone(TaskDoneUpdate taskDoneUpdate) {
        Task task = taskRepository.findById(taskDoneUpdate.id())
                .orElseThrow(() -> new IllegalArgumentException("The given task with id does not exist."));

        task.done(taskDoneUpdate.isDone(), clockHolder);
        taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        if (id == 0) {
            throw new IllegalArgumentException("The given task with id does not exist.");
        }
    }
}
