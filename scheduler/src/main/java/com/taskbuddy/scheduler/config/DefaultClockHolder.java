package com.taskbuddy.scheduler.config;

import com.taskbuddy.scheduler.business.port.ClockHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DefaultClockHolder implements ClockHolder {

    @Override
    public LocalDateTime currentDateTime() {
        return LocalDateTime.now();
    }
}
