package com.taskbuddy.api.controller;

import com.taskbuddy.api.controller.request.TaskCreateRequest;
import com.taskbuddy.api.controller.request.TaskUpdateRequest;
import com.taskbuddy.api.controller.response.ApiResponse;
import com.taskbuddy.api.controller.response.task.TaskResponse;
import com.taskbuddy.core.domain.Task;
import com.taskbuddy.core.domain.TaskCreate;
import com.taskbuddy.core.domain.TaskUpdate;
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

        final Task task = taskService.getTask(id);
        final TaskResponse response = TaskResponse.from(task);

        return ResponseEntity
                .ok(ApiResponse.success(response));
    }

    @PostMapping
    ResponseEntity<ApiResponse<?>> createTask(@RequestBody TaskCreateRequest request) {
        //FIXME 인증 넣으면 제거하기
        final Long dummyUserId = 1L;

        final TaskCreate taskCreate = new TaskCreate(
                dummyUserId,
                request.title(),
                request.description(),
                request.timeFrame().startDateTime(),
                request.timeFrame().endDateTime()
        );

        final Long createdTaskId = taskService.createTask(taskCreate);

        return ResponseEntity
                .created(URI.create("localhost:8080/v1/tasks/" + createdTaskId))
                .body(ApiResponse.success());
    }

    @PatchMapping("/{id}")
    ResponseEntity<ApiResponse<?>> updateTask(@PathVariable("id") Long id, @RequestBody TaskUpdateRequest request) {
        Assert.state(id >= 0, "The id value must be positive.");

        //FIXME 인증 넣으면 제거하기
        final Long dummyUserId = 1L;

        TaskUpdate taskUpdate = new TaskUpdate(
                id,
                dummyUserId,
                request.title(),
                request.description(),
                request.timeFrame().startDateTime(),
                request.timeFrame().endDateTime());
        taskService.updateTask(taskUpdate);

        return ResponseEntity
                .ok(ApiResponse.success());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<?>> deleteTask(@PathVariable("id") Long id) {
        Assert.state(id >= 0, "The id value must be positive.");

        taskService.deleteTask(id);

        return ResponseEntity
                .ok(ApiResponse.success());
    }

}
