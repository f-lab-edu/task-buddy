package com.taskbuddy.core.service;

import com.taskbuddy.core.database.repository.TaskReminderRepository;
import com.taskbuddy.core.domain.Task;
import com.taskbuddy.core.domain.TaskReminder;
import com.taskbuddy.core.service.port.ClockHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TaskReminderReadService {
    private final TaskReminderRepository taskReminderRepository;
    private final TaskService taskService;
    private final UserService userService;
    private final ClockHolder clockHolder;

    public List<TaskReminder> getAllToSendReminder() {
        // 1. 시작일시가 지난 진행중인 (그리고 reminder enabled가 true인) Task 목록 조회
        final List<Task> tasks = taskService.findCurrentTasksWithReminderEnabled();

        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 유저상태가 작업중인지 확인 및 리마인더 설정을 켜놨는지 확인
        final Set<Long> userIds = userService.filterUserIdsWithOnTask(mappedTasks(tasks, Task::getUserId));
        tasks.removeIf(task -> userIds.contains(task.getUserId()));

        // 3. 현재시간이 인터벌 시간이 맞는가?
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
