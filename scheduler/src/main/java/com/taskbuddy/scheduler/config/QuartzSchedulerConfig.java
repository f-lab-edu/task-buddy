package com.taskbuddy.scheduler.config;

import lombok.RequiredArgsConstructor;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@RequiredArgsConstructor
@Configuration
public class QuartzSchedulerConfig {
    private final AutowiringSpringBeanJobFactory jobFactory;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(JobDetail jobDetail, Trigger trigger) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(jobFactory);
        schedulerFactoryBean.setJobDetails(jobDetail);
        schedulerFactoryBean.setTriggers(trigger);
        return schedulerFactoryBean;
    }
}
