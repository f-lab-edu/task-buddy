package com.taskbuddy.api.controller;

import com.taskbuddy.api.utils.JsonUtils;
import com.taskbuddy.core.domain.Task;
import com.taskbuddy.core.domain.TaskReminder;
import com.taskbuddy.core.domain.user.User;
import com.taskbuddy.core.domain.user.UserStatus;
import com.taskbuddy.core.service.TaskReminderReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/v1/task-reminders")
@RestController
public class TaskReminderController {
    private final TaskReminderReadService taskReminderReadService;

    @GetMapping("/to-send")
    public ResponseEntity<List<String>> getAllToSendReminder() {
//        List<TaskReminder> taskReminders = taskReminderReadService.getAllToSendReminder();

        //FIXME 제거하기
        TaskReminder reminder1 = TaskReminder.builder()
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
                .build();

        List<String> result = List.of(reminder1).stream()
                .map(JsonUtils::serialize)
                .toList();

        return ResponseEntity.ok(result);
    }
}
