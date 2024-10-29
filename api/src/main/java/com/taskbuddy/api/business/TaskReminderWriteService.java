package com.taskbuddy.api.business;

import com.taskbuddy.api.business.port.ClockHolder;
import com.taskbuddy.api.domain.Task;
import com.taskbuddy.api.domain.TaskReminder;
import com.taskbuddy.api.persistence.repository.TaskReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TaskReminderWriteService {
    private final TaskReminderRepository taskReminderRepository;
    private final ClockHolder clockHolder;

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

    public void deleteByTaskId(Long taskId) {
        taskReminderRepository.findByTaskId(taskId)
                .ifPresent(taskReminder -> taskReminderRepository.deleteById(taskReminder.getId()));
    }

}
