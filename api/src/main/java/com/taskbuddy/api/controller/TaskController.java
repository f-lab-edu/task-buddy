package com.taskbuddy.api.controller;

import com.taskbuddy.api.controller.request.TaskCreateRequest;
import com.taskbuddy.api.controller.request.TaskUpdateRequest;
import com.taskbuddy.api.controller.response.ApiResponse;
import com.taskbuddy.api.controller.response.task.TaskResponse;
import com.taskbuddy.core.domain.Task;
import com.taskbuddy.core.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/v1/tasks")
@RestController
public class TaskController {
    private final TaskService taskService;

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<TaskResponse>> getTask(@PathVariable("id") Long id) {
        Assert.state(id >= 0, "The id value must be positive.");

        Task task = taskService.getTask(id);
        TaskResponse response = TaskResponse.from(task);

        return ResponseEntity
                .ok(ApiResponse.success(response));
    }

    @PostMapping
    ResponseEntity<ApiResponse<?>> createTask(@RequestBody TaskCreateRequest request) {
        //Dummy
        final long createdTaskId = 1L;

        return ResponseEntity
                .created(URI.create("localhost:8080/v1/tasks/" + createdTaskId))
                .body(ApiResponse.success());
    }

    @PatchMapping("/{id}")
    ResponseEntity<ApiResponse<?>> updateTask(@PathVariable("id") Long id, @RequestBody TaskUpdateRequest request) {
        Assert.state(id >= 0, "The id value must be positive.");

        // FIXME 서비스 로직 구현하면 제거하기
        if (id == 0) {
            throw new IllegalArgumentException("The given task with id does not exist.");
        }

        return ResponseEntity
                .ok(ApiResponse.success());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<?>> removeTask(@PathVariable("id") Long id) {
        Assert.state(id >= 0, "The id value must be positive.");

        // FIXME 서비스 로직 구현하면 제거하기
        if (id == 0) {
            throw new IllegalArgumentException("The given task with id does not exist.");
        }

        return ResponseEntity
                .ok(ApiResponse.success());
    }

}
