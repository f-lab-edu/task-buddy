package com.taskbuddy.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RequiredArgsConstructor
@Component
public class TaskReminderScheduler {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void execute() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/v1/task-reminders/to-send", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException();
        }

        String body = response.getBody();
        if (body == null) {
            return;
        }

        List<String> allToSendReminder = objectMapper.readValue(body, new TypeReference<>() {});
        for (String reminder : allToSendReminder) {
            kafkaTemplate.send("task-reminders", reminder);
        }
    }
}
