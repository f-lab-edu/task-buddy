package com.taskbuddy.api.persistence.repository;

import com.taskbuddy.persistence.entity.TaskReminderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskReminderJpaRepository extends JpaRepository<TaskReminderEntity, Long> {
    Optional<TaskReminderEntity> findByTaskId(Long taskId);
}
