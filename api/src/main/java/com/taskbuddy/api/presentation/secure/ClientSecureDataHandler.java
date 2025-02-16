package com.taskbuddy.api.presentation.secure;

import org.springframework.stereotype.Component;

@Component
public class ClientSecureDataHandler {

    // 개인정보와 같이 보안정보를 Client -> Server 통신할 때 건네줌
    public String decode(String value) {

        return value;
    }
}
