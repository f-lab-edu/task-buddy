package com.taskbuddy.core.database.repository;

import com.taskbuddy.core.domain.TaskReminder;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//임시생성
@Repository
public class DefaultTaskReminderRepository implements TaskReminderRepository {

    @Override
    public Optional<TaskReminder> findByTaskId(Long taskId) {
        return Optional.empty();
    }

    @Override
    public void save(TaskReminder taskReminder) {

    }

    @Override
    public void deleteById(Long id) {

    }
}
