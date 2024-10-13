package com.taskbuddy.core.service;

import com.taskbuddy.core.database.repository.TaskReminderRepository;
import com.taskbuddy.core.domain.TaskReminder;
import com.taskbuddy.core.domain.Task;
import com.taskbuddy.core.service.port.ClockHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TaskReminderService {
    private final TaskReminderRepository taskReminderRepository;
    private final ClockHolder clockHolder;

    public TaskReminder getByTaskId(Long taskId) {
        return taskReminderRepository.findByTaskId(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task Settings with given task id does not exist."));
    }

    public void initialize(Task task, Duration reminderInterval) {
        if (!task.isReminderEnabled()) {
            return;
        }

        TaskReminder taskReminder = TaskReminder.from(task, reminderInterval, clockHolder);
        taskReminderRepository.save(taskReminder);
    }

    public void update(Task task, Duration reminderInterval) {
        Optional<TaskReminder> optionalTaskReminder = taskReminderRepository.findByTaskId(task.getId());

        if (optionalTaskReminder.isEmpty()) {
            initialize(task, reminderInterval);
            return;
        }

        TaskReminder taskReminder = optionalTaskReminder.get();

        if (!task.isReminderEnabled()) {
            taskReminderRepository.deleteById(taskReminder.getId());
        } else {
            taskReminder.updateReminderInterval(reminderInterval, clockHolder);
            taskReminderRepository.save(taskReminder);
        }
    }

    public void updateLastSentTime(TaskReminder taskReminder, LocalDateTime lastSentTime) {
        taskReminder.updateLastReminderSentTime(lastSentTime, clockHolder);
    }

    public void deleteByTaskId(Long taskId) {
        taskReminderRepository.findByTaskId(taskId)
                .ifPresent(taskReminder -> taskReminderRepository.deleteById(taskReminder.getId()));
    }
}
