package com.taskbuddy.api.presentation.secure;

import com.taskbuddy.api.config.ApplicationProfile;
import com.taskbuddy.api.utils.JsonUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile(ApplicationProfile.LOCAL)
@Component
public class NoOpSecureDataDecryptor implements SecureDataDecryptor {

    @Override
    public <R> R decrypt(String encryptedData, Class<R> returnClass) {
        return JsonUtils.deserialize(encryptedData, returnClass);
    }
}
