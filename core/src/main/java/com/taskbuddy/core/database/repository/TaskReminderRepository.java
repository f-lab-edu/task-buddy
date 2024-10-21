package com.taskbuddy.core.database.repository;

import com.taskbuddy.core.domain.TaskReminder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TaskReminderRepository {

    Optional<TaskReminder> findByTaskId(Long taskId);

    List<TaskReminder> findAllInTaskIds(Set<Long> taskIds);

    void save(TaskReminder taskReminder);

    void deleteById(Long id);
}
