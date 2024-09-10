package com.taskbuddy.core.database.repository;

import com.taskbuddy.core.domain.Task;
import com.taskbuddy.core.service.port.TaskRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MySQLTaskRepository implements TaskRepository {

    @Override
    public Optional<Task> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Task save(Task task) {
        return null;
    }
}
