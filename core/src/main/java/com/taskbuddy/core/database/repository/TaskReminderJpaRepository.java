package com.taskbuddy.core.database.repository;

import com.taskbuddy.core.database.entity.TaskReminderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskReminderJpaRepository extends JpaRepository<TaskReminderEntity, Long> {
}
