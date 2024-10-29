package com.taskbuddy.api.persistence.repository;

import com.taskbuddy.persistence.entity.TaskReminderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskReminderJpaRepository extends JpaRepository<TaskReminderEntity, Long> {
}
