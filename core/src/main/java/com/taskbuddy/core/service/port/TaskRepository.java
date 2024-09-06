package com.taskbuddy.core.service.port;

import com.taskbuddy.core.domain.Task;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository {

    Optional<Task> findById(Long id);
}
