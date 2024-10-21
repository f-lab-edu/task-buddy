package com.taskbuddy.api.controller;

import com.taskbuddy.api.utils.JsonUtils;
import com.taskbuddy.core.domain.TaskReminder;
import com.taskbuddy.core.service.TaskReminderReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("/v1/task-reminders")
@RestController
public class TaskReminderController {
    private final TaskReminderReadService taskReminderReadService;

    @GetMapping("/to-send")
    public ResponseEntity<List<String>> getAllToSendReminder() {
        List<TaskReminder> taskReminders = taskReminderReadService.getAllToSendReminder();

        List<String> result = taskReminders.stream()
                .map(JsonUtils::serialize)
                .toList();

        return ResponseEntity.of(Optional.of(result));
    }
}
