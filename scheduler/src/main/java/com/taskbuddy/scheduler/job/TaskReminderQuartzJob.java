package com.taskbuddy.scheduler.job;

import com.taskbuddy.core.domain.TaskReminder;
import com.taskbuddy.core.service.TaskReminderReadService;
import com.taskbuddy.queue.message.TaskReminderMessage;
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
    private final TaskReminderReadService taskReminderReadService;

    @Override
    public void execute(JobExecutionContext context) {
        log.info("TaskReminderQuartzJob.execute");

        List<TaskReminder> taskReminders = taskReminderReadService.getAllToSendReminder();

        for (TaskReminder taskReminder : taskReminders) {
            TaskReminderMessage message = TaskReminderMessage.builder()
                    .id(taskReminder.getId())
                    .title(taskReminder.getTask().getTitle())
                    .build();

            System.out.println("taskReminderMessage.toString() = " + message);
            kafkaTemplate.send("task-reminders", message);
        }
    }
}
