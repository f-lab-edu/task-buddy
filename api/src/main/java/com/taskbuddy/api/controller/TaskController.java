package com.taskbuddy.api.controller;

import com.taskbuddy.api.controller.request.TaskCreateRequest;
import com.taskbuddy.api.controller.response.ApiResponse;
import com.taskbuddy.api.controller.response.NoData;
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

    @GetMapping
    ApiResponse<Object> getAll() {

        return null;
    }

    //TODO Response 클래스명이 API 응답 클래스라는게 잘 드러나는가?
    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<TaskResponse>> getOne(@PathVariable("id") Long id) {
        Assert.notNull(id, "The id argument must not be null.");
        Assert.state(id >= 0, "The id value must be positive.");

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

    //TODO RequestBody, RequestAttribute 차이점, 그리고 꼭 붙여야하는지
    @PostMapping
    ResponseEntity<ApiResponse<NoData>> create(@RequestBody TaskCreateRequest request) {
        Assert.notNull(request, "Teh request argument must not be null.");

        //Dummy
        final long createdTaskId = 1L;

        return ResponseEntity
                .created(URI.create("localhost:8080/v1/tasks/" + createdTaskId))
                .body(ApiResponse.success());
    }

    @PatchMapping("/{id}")
    void update(@PathVariable Long id) {

    }

    @PatchMapping("/{id}/done")
    void checkedTask() {

    }

    @DeleteMapping("/{id}")
    void remove(@PathVariable Long id) {

    }

}
