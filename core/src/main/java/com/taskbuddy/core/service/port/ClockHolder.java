package com.taskbuddy.core.service.port;

import java.time.LocalDateTime;

public interface ClockHolder {

    LocalDateTime currentDateTime();
}
