package com.taskbuddy.api.persistence.repository;

import com.taskbuddy.api.domain.TaskReminder;

import java.util.Optional;

public interface TaskReminderRepository {

    Optional<TaskReminder> findByTaskId(Long taskId);

    void save(TaskReminder taskReminder);

    void deleteById(Long id);
}
