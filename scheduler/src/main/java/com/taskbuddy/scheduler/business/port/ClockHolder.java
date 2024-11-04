package com.taskbuddy.scheduler.business.port;

import java.time.LocalDateTime;

public interface ClockHolder {

    LocalDateTime currentDateTime();
}
