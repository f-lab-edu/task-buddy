package com.taskbuddy.core.database.repository;

import com.taskbuddy.core.domain.ReminderSettings;

import java.util.Optional;

public interface ReminderSettingsRepository {

    Optional<ReminderSettings> findByTaskId(Long taskId);

    void save(ReminderSettings reminderSettings);

    void deleteById(Long id);
}
