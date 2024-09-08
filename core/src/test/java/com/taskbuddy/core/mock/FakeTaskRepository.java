package com.taskbuddy.core.mock;

import com.taskbuddy.core.domain.Task;
import com.taskbuddy.core.service.port.TaskRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeTaskRepository implements TaskRepository {
    private final Map<Long, Task> data = new HashMap<>();
    private long generatedId;

    public Task getById(Long id) {
        return data.get(id);
    }

    @Override
    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Task save(Task task) {
        if (task.getId() == null) {
            final long id = ++generatedId;
            Task savedTask = Task.builder()
                    .id(id)
                    .title(task.getTitle())
                    .isDone(task.getIsDone())
                    .description(task.getDescription())
                    .timeFrame(task.getTimeFrame())
                    .createdAt(task.getCreatedAt())
                    .updatedAt(task.getUpdatedAt())
                    .build();
            data.put(id, savedTask);
            return savedTask;
        } else {
            data.put(task.getId(), task);
            return task;
        }
    }
}
