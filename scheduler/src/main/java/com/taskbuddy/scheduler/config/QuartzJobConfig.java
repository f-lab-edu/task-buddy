package com.taskbuddy.scheduler.config;

import com.taskbuddy.scheduler.job.TaskReminderQuartzJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzJobConfig {

    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob(TaskReminderQuartzJob.class)
                .withIdentity("taskReminderJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger trigger(JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("taskReminderTrigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(10)
                        .repeatForever())
                .build();
    }
}
