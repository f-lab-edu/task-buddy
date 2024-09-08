package com.taskbuddy.core.service;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public interface ClockHolder {

    LocalDateTime currentDateTime();
}
