package com.taskbuddy.scheduler.business;

import com.taskbuddy.scheduler.business.port.ClockHolder;
import com.taskbuddy.scheduler.business.port.TaskRepository;
import com.taskbuddy.scheduler.domain.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ClockHolder clockHolder;

    public Task getTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("The given task with id does not exist."));
    }

    public List<Task> findCurrentTasksWithReminderEnabled() {
        return taskRepository.findAllInTimeFrameAndReminderEnabled(true, clockHolder.currentDateTime());
    }
}
