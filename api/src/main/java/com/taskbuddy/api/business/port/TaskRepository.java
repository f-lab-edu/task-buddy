package com.taskbuddy.api.business.port;

import com.taskbuddy.api.domain.Task;

import java.util.Optional;

public interface TaskRepository {

    boolean existsById(Long id);

    Optional<Task> findById(Long id);

    Task save(Task task);

    void deleteById(Long id);
}
