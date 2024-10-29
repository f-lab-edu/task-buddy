package com.taskbuddy.scheduler.business;

import com.taskbuddy.scheduler.business.port.ClockHolder;
import com.taskbuddy.scheduler.business.port.TaskReminderRepository;
import com.taskbuddy.scheduler.domain.Task;
import com.taskbuddy.scheduler.domain.TaskReminder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TaskReminderService {
    private final TaskReminderRepository taskReminderRepository;
    private final TaskService taskService;
    private final UserService userService;
    private final ClockHolder clockHolder;

    public List<TaskReminder> getAllToSendReminder() {
        final List<Task> tasks = taskService.findCurrentTasksWithReminderEnabled();

        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }

        final Set<Long> userIds = userService.filterUserIdsWithOnTask(mappedTasks(tasks, Task::getUserId));
        tasks.removeIf(task -> userIds.contains(task.getUserId()));

        return taskReminderRepository.findAllInTaskIds(mappedTasks(tasks, Task::getId))
                .stream()
                .filter(taskReminder -> taskReminder.isReminderDue(clockHolder))
                .toList();
    }

    private Set<Long> mappedTasks(List<Task> tasks, Function<Task, Long> mapper) {
        return tasks.stream()
                .map(mapper)
                .collect(Collectors.toSet());
    }
}
