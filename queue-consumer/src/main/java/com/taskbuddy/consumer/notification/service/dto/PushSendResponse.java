package com.taskbuddy.consumer.notification.service.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PushSendResponse {
    public boolean sentSuccessfully;
    public LocalDateTime sentDateTime;

    public PushSendResponse(boolean sentSuccessfully, LocalDateTime sentDateTime) {
        this.sentSuccessfully = sentSuccessfully;
        this.sentDateTime = sentDateTime;
    }
}
