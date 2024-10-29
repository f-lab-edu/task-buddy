package com.taskbuddy.api.business;

import com.taskbuddy.api.business.port.ClockHolder;
import com.taskbuddy.api.domain.Task;
import com.taskbuddy.api.domain.TaskReminder;
import com.taskbuddy.api.domain.user.User;
import com.taskbuddy.api.domain.user.UserStatus;
import com.taskbuddy.api.persistence.repository.TaskReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
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
        return List.of(TaskReminder.builder()
                .id(1L)
                .task(Task.builder()
                        .id(1L)
                        .user(User.builder()
                                .id(1L)
                                .status(UserStatus.DISCONNECTED)
                                .build())
                        .title("Task title")
                        .build())
                .reminderInterval(Duration.ofMinutes(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

//        final List<Task> tasks = taskService.findCurrentTasksWithReminderEnabled();

//        if (tasks.isEmpty()) {
//            return Collections.emptyList();
//        }

//        final Set<Long> userIds = userService.filterUserIdsWithOnTask(mappedTasks(tasks, Task::getUserId));
//        tasks.removeIf(task -> userIds.contains(task.getUserId()));

//        return taskReminderRepository.findAllInTaskIds(mappedTasks(tasks, Task::getId))
//                .stream()
//                .filter(taskReminder -> taskReminder.isReminderDue(clockHolder))
//                .toList();



    }

    private Set<Long> mappedTasks(List<Task> tasks, Function<Task, Long> mapper) {
        return tasks.stream()
                .map(mapper)
                .collect(Collectors.toSet());
    }
}
