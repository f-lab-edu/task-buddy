package com.taskbuddy.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class TaskReminderScheduler {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Scheduled(cron = "0/5 * * * * ?")
    public void execute() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        // TODO API통신 (API가 받을 수 없을 때의 대비 로직 추가하기)
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/v1/task-reminders/to-send", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException();
        }

        String body = response.getBody();
        if (body == null) {
            return;
        }

        List<String> allRemindersToSend = objectMapper.readValue(body, new TypeReference<>() {});

        for (String message : allRemindersToSend) {
            kafkaTemplate.send("task-reminders", objectMapper.writeValueAsString(message));
        }
    }
}
