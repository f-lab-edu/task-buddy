package com.taskbuddy.api.controller;

import com.taskbuddy.api.controller.request.TaskCreateRequest;
import com.taskbuddy.api.controller.request.TaskUpdateRequest;
import com.taskbuddy.api.controller.response.ApiResponse;
import com.taskbuddy.api.controller.response.task.TaskResponse;
import com.taskbuddy.api.controller.response.task.TimeFrame;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;

@RequestMapping("/v1/tasks")
@RestController
public class TaskController {
    // TODO spring boot validation 추가가 낫겠다. 일단 던지는 Exception에 대헤서 처리하고, Validation 도입하면 같이 붙여야지
    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<TaskResponse>> getTask(@PathVariable("id") Long id) {
        Assert.state(id >= 0, "The id value must be positive.");

        // TODO Custom Exception 구현하기
        // FIXME 서비스 로직 구현하면 제거하기
        if (id == 0) {
            throw new IllegalArgumentException("The given task with id does not exist.");
        }

        //Dummy
        TaskResponse response = new TaskResponse(
                1L
                , "알고리즘 문제 풀기"
                , "백준1902..."
                , false
                , new TimeFrame(
                        LocalDateTime.of(2024, 8, 1, 0, 0, 0)
                        , LocalDateTime.of(2024, 8, 31, 23, 59, 59)));

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
