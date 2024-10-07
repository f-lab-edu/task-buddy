package com.taskbuddy.core.service;

import com.taskbuddy.core.database.repository.ReminderSettingsRepository;
import com.taskbuddy.core.domain.ReminderSettings;
import com.taskbuddy.core.domain.Task;
import com.taskbuddy.core.service.port.ClockHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReminderSettingsService {
    private final ReminderSettingsRepository reminderSettingsRepository;
    private final ClockHolder clockHolder;

    public ReminderSettings getByTaskId(Long taskId) {
        return reminderSettingsRepository.findByTaskId(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task Settings with given task id does not exist."));
    }


    public void initialize(Task task, Duration reminderInterval) {
        if (!task.isReminderEnabled()) {
            return;
        }

        ReminderSettings reminderSettings = ReminderSettings.from(task, reminderInterval, clockHolder);
        reminderSettingsRepository.save(reminderSettings);
    }

    public void update(Task task, Duration reminderInterval) {
        Optional<ReminderSettings> optionalReminderSettings = reminderSettingsRepository.findByTaskId(task.getId());

        if (optionalReminderSettings.isEmpty()) {
            initialize(task, reminderInterval);
            return;
        }

        ReminderSettings reminderSettings = optionalReminderSettings.get();

        if (!task.isReminderEnabled()) {
            reminderSettingsRepository.deleteById(reminderSettings.getId());
        } else {
            reminderSettings.updateReminderInterval(reminderInterval, clockHolder);
            reminderSettingsRepository.save(reminderSettings);
        }
    }

    public void updateLastSentTime(ReminderSettings reminderSettings, LocalDateTime lastSentTime) {
        reminderSettings.updateLastReminderSentTime(lastSentTime, clockHolder);
    }

    public void deleteByTaskId(Long taskId) {
        reminderSettingsRepository.findByTaskId(taskId)
                .ifPresent(reminderSettings -> reminderSettingsRepository.deleteById(reminderSettings.getId()));
    }
}
