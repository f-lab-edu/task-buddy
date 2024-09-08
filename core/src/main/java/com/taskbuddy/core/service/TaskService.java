package com.taskbuddy.core.service;

import com.taskbuddy.core.domain.Task;
import com.taskbuddy.core.domain.TaskCreate;
import com.taskbuddy.core.domain.TaskUpdate;
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
//        final Task task = Task.from(taskCreate);
//        final Task savedTask = taskRepository.save(task);

//        return savedTask.id();

        return 1L;
    }

    public void updateTask(TaskUpdate taskUpdate) {
        if (taskUpdate.id() == 0) {
            throw new IllegalArgumentException("The given task with id does not exist.");
        }
    }

    public void deleteTask(Long id) {
        if (id == 0) {
            throw new IllegalArgumentException("The given task with id does not exist.");
        }
    }
}
