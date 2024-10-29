package com.taskbuddy.scheduler.persistence.repository;

import com.taskbuddy.scheduler.business.port.TaskReminderRepository;
import com.taskbuddy.scheduler.domain.TaskReminder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

//임시생성
@RequiredArgsConstructor
@Repository
public class DefaultTaskReminderRepository implements TaskReminderRepository {
    private final TaskReminderJpaRepository taskReminderJpaRepository;

    @Override
    public List<TaskReminder> findAllInTaskIds(Set<Long> taskIds) {
//        return taskReminderJpaRepository.findAll().stream()
//                .filter(entity -> taskIds.contains(entity.getTaskId()))
//                .map(TaskReminderEntity::toModel)
//                .toList();
        return null;
    }
}
