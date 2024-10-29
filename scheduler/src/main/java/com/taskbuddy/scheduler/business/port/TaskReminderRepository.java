package com.taskbuddy.scheduler.business.port;

import com.taskbuddy.scheduler.domain.TaskReminder;

import java.util.List;
import java.util.Set;

public interface TaskReminderRepository {

    List<TaskReminder> findAllInTaskIds(Set<Long> taskIds);
}
