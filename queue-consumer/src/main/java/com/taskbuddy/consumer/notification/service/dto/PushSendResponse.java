package com.taskbuddy.consumer.notification.service.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PushSendResponse {
    private Long id;
    private boolean sentSuccessfully;
    private LocalDateTime sentDateTime;

    public PushSendResponse(Long id, boolean sentSuccessfully, LocalDateTime sentDateTime) {
        this.id = id;
        this.sentSuccessfully = sentSuccessfully;
        this.sentDateTime = sentDateTime;
    }
}
