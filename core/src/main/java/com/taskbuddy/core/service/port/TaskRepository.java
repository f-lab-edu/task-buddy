package com.taskbuddy.core.service.port;

import com.taskbuddy.core.domain.Task;

import java.util.Optional;

public interface TaskRepository {

    boolean existsById(Long id);

    Optional<Task> findById(Long id);

    Task save(Task task);

    void deleteById(Long id);
}
