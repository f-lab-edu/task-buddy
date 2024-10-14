package com.taskbuddy.core.database.repository;

import com.taskbuddy.core.database.entity.TaskEntity;
import com.taskbuddy.core.domain.Task;
import com.taskbuddy.core.service.port.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
                .map(TaskEntity::toModel);
    }

    @Override
    public List<Task> findAllInTimeFrameAndReminderEnabled(boolean isReminderEnabled, LocalDateTime dateTime) {
        return taskJpaRepository.findAll().stream()
                .map(TaskEntity::toModel)
                .filter(task -> task.isReminderEnabled() &&
                        dateTime.isAfter(task.getTimeFrame().startDateTime()) &&
                        dateTime.isBefore(task.getTimeFrame().endDateTime()))
                .toList();
    }

    @Override
    public Task save(Task task) {
        return taskJpaRepository.save(TaskEntity.from(task))
                .toModel();
    }

    @Override
    public void deleteById(Long id) {
        taskJpaRepository.deleteById(id);
    }
}
