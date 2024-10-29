package com.taskbuddy.scheduler.business.port;

import com.taskbuddy.scheduler.domain.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    Optional<Task> findById(Long id);

    List<Task> findAllInTimeFrameAndReminderEnabled(boolean enabledReminder, LocalDateTime dateTime);
}
