package com.taskbuddy.core.service;

import java.time.LocalDateTime;

public class ClockHolderImpl implements ClockHolder {

    @Override
    public LocalDateTime currentDateTime() {
        return LocalDateTime.now();
    }
}
