package com.taskbuddy.api.controller;

import com.taskbuddy.api.controller.response.ApiResponse;
import com.taskbuddy.api.controller.response.task.TaskResponse;
import com.taskbuddy.api.controller.response.task.TimeFrame;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

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
    ApiResponse<TaskResponse> getOne(@PathVariable("id") Long id) {
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

        return ApiResponse.success(response);
    }

    @PostMapping
    void create() {

    }

    @PatchMapping("/{id}")
    void update(@PathVariable Long id) {

    }

    @DeleteMapping("/{id}")
    void remove(@PathVariable Long id) {

    }

}
