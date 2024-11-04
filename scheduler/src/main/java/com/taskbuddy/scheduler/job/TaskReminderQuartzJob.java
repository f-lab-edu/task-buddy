package com.taskbuddy.scheduler.job;

import com.taskbuddy.queue.message.TaskReminderMessage;
import com.taskbuddy.queue.topic.Topics;
import com.taskbuddy.scheduler.business.TaskReminderService;
import com.taskbuddy.scheduler.domain.TaskReminder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class TaskReminderQuartzJob implements Job {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TaskReminderService TaskReminderService;

    @Override
    public void execute(JobExecutionContext context) {
        List<TaskReminder> taskReminders = TaskReminderService.getAllToSendReminder();

        for (TaskReminder taskReminder : taskReminders) {
            TaskReminderMessage message = TaskReminderMessage.builder()
                    .id(taskReminder.getId())
                    .title(taskReminder.getTask().getTitle())
                    .build();

            kafkaTemplate.send(Topics.TaskReminder.PUSH, message);
        }
    }
}
