package com.taskbuddy.core.database.repository;

import com.taskbuddy.core.domain.ReminderSettings;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//임시생성
@Repository
public class DefaultReminderSettingsRepository implements ReminderSettingsRepository {

    @Override
    public Optional<ReminderSettings> findByTaskId(Long taskId) {
        return Optional.empty();
    }

    @Override
    public void save(ReminderSettings reminderSettings) {

    }

    @Override
    public void deleteById(Long id) {

    }
}
