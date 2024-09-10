package com.taskbuddy.core.mock;

import com.taskbuddy.core.service.port.ClockHolder;

import java.time.LocalDateTime;

public class TestClockHolder implements ClockHolder {
    private LocalDateTime currentDateTime;

    public TestClockHolder(LocalDateTime currentDateTime) {
        this.currentDateTime = currentDateTime;
    }

    @Override
    public LocalDateTime currentDateTime() {
        return currentDateTime;
    }
}
