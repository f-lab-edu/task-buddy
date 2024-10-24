package com.taskbuddy.core.service.port;

import com.taskbuddy.core.domain.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    boolean existsById(Long id);

    Optional<Task> findById(Long id);

    List<Task> findAllInTimeFrameAndReminderEnabled(boolean enabledReminder, LocalDateTime dateTime);

    Task save(Task task);

    void deleteById(Long id);
}
