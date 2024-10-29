package com.taskbuddy.api.persistence.repository;

import com.taskbuddy.api.business.port.TaskRepository;
import com.taskbuddy.api.domain.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// TODO 효율적인가?
@RequiredArgsConstructor
@Repository
public class DefaultTaskRepository implements TaskRepository {
    private final TaskJpaRepository taskJpaRepository;

    @Override
    public boolean existsById(Long id) {
        return taskJpaRepository.existsById(id);
    }

    @Override
    public Optional<Task> findById(Long id) {
        return taskJpaRepository.findById(id)
                .map(Task::from);
    }

    @Override
    public Task save(Task task) {
        return Task.from(taskJpaRepository.save(task.toEntity()));
    }

    @Override
    public void deleteById(Long id) {
        taskJpaRepository.deleteById(id);
    }
}
