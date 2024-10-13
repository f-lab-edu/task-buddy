package com.taskbuddy.core.database.repository;

import com.taskbuddy.core.domain.TaskReminder;

import java.util.Optional;

public interface TaskReminderRepository {

    Optional<TaskReminder> findByTaskId(Long taskId);

    void save(TaskReminder taskReminder);

    void deleteById(Long id);
}
