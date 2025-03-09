package com.taskbuddy.api.presentation.secure;

import com.taskbuddy.api.utils.JsonUtils;
import org.springframework.stereotype.Component;

@Component
public class ClientSecureDataHandler {

    // 개인정보와 같이 보안정보를 Client -> Server 통신할 때 건네줌
    public <R> R decode(String value, Class<R> returnClass) {
        // FIXME 임시로직
        return JsonUtils.deserialize(value, returnClass);
    }
}
