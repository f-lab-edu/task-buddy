package com.taskbuddy.core.service;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ClockHolderImpl implements ClockHolder {

    @Override
    public LocalDateTime currentDateTime() {
        return LocalDateTime.now();
    }
}
