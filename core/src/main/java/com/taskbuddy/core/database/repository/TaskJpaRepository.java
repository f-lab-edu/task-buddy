package com.taskbuddy.core.database.repository;

import com.taskbuddy.core.database.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskJpaRepository extends JpaRepository<TaskEntity, Long> {

}
