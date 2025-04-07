package com.taskbuddy.api.business.user;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class BcryptPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(String plainPassword) {
        String salt = BCrypt.gensalt(12);
        return BCrypt.hashpw(plainPassword, salt);
    }

    @Override
    public boolean matches(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
