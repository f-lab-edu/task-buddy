package com.taskbuddy.core.service;

import com.taskbuddy.core.domain.Task;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TaskService {

    public Task getTask(Long id) {
//        Task task = taskRepository.findById()
//                .orElseThrow(() -> new IllegalArgumentException("The given task with id does not exist."));

        if (id == 0) {
            throw new IllegalArgumentException("The given task with id does not exist.");
        }

        // Dummy
        return new Task(1L,
                "알고리즘 문제 풀기",
                "백준1902...",
                false,
                LocalDateTime.of(2024, 8, 1, 0, 0, 0),
                LocalDateTime.of(2024, 8, 31, 23, 59, 59));
    }
}
