package com.taskbuddy.api.business;

import com.taskbuddy.api.business.port.ClockHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DefaultClockHolder implements ClockHolder {

    @Override
    public LocalDateTime currentDateTime() {
        return LocalDateTime.now();
    }
}
