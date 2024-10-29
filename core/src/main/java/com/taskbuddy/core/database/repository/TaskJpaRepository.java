package com.taskbuddy.core.database.repository;

import com.taskbuddy.persistence.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskJpaRepository extends JpaRepository<TaskEntity, Long> {

}
