package com.taskbuddy.core.service;

import com.taskbuddy.core.domain.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    //임시 구현
    public boolean isUserLoggedIn(User user) {
        return user.isLoggedIn();
    }
}
