package com.taskbuddy.scheduler.persistence.repository;

import com.taskbuddy.scheduler.business.port.TaskRepository;
import com.taskbuddy.scheduler.domain.Task;
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
    public Optional<Task> findById(Long id) {
//        return taskJpaRepository.findById(id)
//                .map(TaskEntity::toModel);

        return null;
    }

    @Override
    public List<Task> findAllInTimeFrameAndReminderEnabled(boolean isReminderEnabled, LocalDateTime dateTime) {
//        return taskJpaRepository.findAll().stream()
//                .map(TaskEntity::toModel)
//                .filter(task -> task.isReminderEnabled() &&
//                        dateTime.isAfter(task.getTimeFrame().startDateTime()) &&
//                        dateTime.isBefore(task.getTimeFrame().endDateTime()))
//                .toList();

        return null;
    }
}
