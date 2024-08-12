package com.taskbuddy.api.hello;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("/health-check")
    public void healthCheck() {}
}
