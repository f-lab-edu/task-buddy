package com.taskbuddy.core.mock;

import com.taskbuddy.core.domain.Task;
import com.taskbuddy.core.service.port.TaskRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeTaskRepository implements TaskRepository {
    private final Map<Long, Task> data = new HashMap<>();
    private long generatedId;

    public void save(Task task) {
        if (task.id() == null) {
            data.put(++generatedId,
                    new Task(
                        generatedId,
                        task.title(),
                        task.description(),
                        task.isDone(),
                        task.timeFrame()));
        } else {
            data.put(task.id(), task);
        }
    }

    @Override
    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(data.get(id));
    }
}
