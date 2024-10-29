package com.taskbuddy.api.persistence.repository;

import com.taskbuddy.api.domain.TaskReminder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//임시생성
@RequiredArgsConstructor
@Repository
public class DefaultTaskReminderRepository implements TaskReminderRepository {
    private final TaskReminderJpaRepository taskReminderJpaRepository;

    @Override
    public Optional<TaskReminder> findByTaskId(Long taskId) {
        return taskReminderJpaRepository.findAll().stream()
                .filter(entity -> entity.getTaskId().equals(taskId))
                .map(TaskReminder::from)
                .findFirst();
    }

    @Override
    public void save(TaskReminder taskReminder) {
        taskReminderJpaRepository.save(taskReminder.toEntity());
    }

    @Override
    public void deleteById(Long id) {
        taskReminderJpaRepository.deleteById(id);
    }
}
