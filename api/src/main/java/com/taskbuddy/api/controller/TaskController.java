package com.taskbuddy.api.controller;

import com.taskbuddy.api.controller.request.TaskContentUpdateRequest;
import com.taskbuddy.api.controller.request.TaskCreateRequest;
import com.taskbuddy.api.controller.request.TaskDoneUpdateRequest;
import com.taskbuddy.api.controller.response.ApiResponse;
import com.taskbuddy.api.controller.response.task.TaskResponse;
import com.taskbuddy.core.domain.Task;
import com.taskbuddy.core.domain.TaskContentUpdate;
import com.taskbuddy.core.domain.TaskCreate;
import com.taskbuddy.core.domain.TaskDoneUpdate;
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
        //FIXME (#15) 인증 넣으면 제거하기
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

    @PatchMapping("/{id}/content")
    ResponseEntity<ApiResponse<?>> updateTaskContent(@PathVariable("id") Long id, @RequestBody TaskContentUpdateRequest request) {
        Assert.state(id >= 0, "The id value must be positive.");

        //FIXME (#15) 인증 넣으면 제거하기
        final Long dummyUserId = 1L;

        TaskContentUpdate taskContentUpdate = new TaskContentUpdate(
                id,
                dummyUserId,
                request.title(),
                request.description(),
                request.timeFrame().startDateTime(),
                request.timeFrame().endDateTime());
        taskService.updateContent(taskContentUpdate);

        return ResponseEntity
                .ok(ApiResponse.success());
    }

    @PatchMapping("/{id}/is-done")
    ResponseEntity<ApiResponse<?>> updateTaskDone(@PathVariable("id") Long id, @RequestBody TaskDoneUpdateRequest request) {
        Assert.state(id >= 0, "The id value must be positive.");

        //FIXME (#15) 인증 넣으면 제거하기
        final Long dummyUserId = 1L;

        TaskDoneUpdate taskDoneUpdate = new TaskDoneUpdate(id, dummyUserId, request.isDone());
        taskService.updateDone(taskDoneUpdate);

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
