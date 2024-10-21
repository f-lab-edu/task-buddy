package com.taskbuddy.scheduler;

import com.taskbuddy.api.utils.JsonUtils;
import com.taskbuddy.core.domain.TaskReminder;
import com.taskbuddy.core.service.TaskReminderReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class TaskReminderScheduler {
    private final TaskReminderReadService taskReminderReadService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void execute() {
        List<TaskReminder> taskReminders = taskReminderReadService.getAllToSendReminder();

        for (TaskReminder taskReminder : taskReminders) {
            kafkaTemplate.send("task-reminders", JsonUtils.serialize(taskReminder));
        }
    }
}
