package com.taskbuddy.core.database.repository;

import com.taskbuddy.core.domain.TaskReminder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

//임시생성
@RequiredArgsConstructor
@Repository
public class DefaultTaskReminderRepository implements TaskReminderRepository {
    private final TaskReminderJpaRepository taskReminderJpaRepository;

    @Override
    public Optional<TaskReminder> findByTaskId(Long taskId) {
//        return taskReminderJpaRepository.findAll().stream()
//                .filter(entity -> entity.getTaskId().equals(taskId))
//                .map(TaskReminderEntity::toModel)
//                .findFirst();

        return null;
    }

    @Override
    public List<TaskReminder> findAllInTaskIds(Set<Long> taskIds) {
//        return taskReminderJpaRepository.findAll().stream()
//                .filter(entity -> taskIds.contains(entity.getTaskId()))
//                .map(TaskReminderEntity::toModel)
//                .toList();
        return null;
    }

    @Override
    public void save(TaskReminder taskReminder) {
//        taskReminderJpaRepository.save(TaskReminderEntity.from(taskReminder));
    }

    @Override
    public void deleteById(Long id) {
        taskReminderJpaRepository.deleteById(id);
    }
}
