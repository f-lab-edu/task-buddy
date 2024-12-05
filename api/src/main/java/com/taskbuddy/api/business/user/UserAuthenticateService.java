package com.taskbuddy.api.business.user;

import com.taskbuddy.api.persistence.repository.UserJpaRepository;
import com.taskbuddy.api.presentation.user.request.UserSignupRequest;
import com.taskbuddy.api.presentation.user.response.UserSignupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserAuthenticateService {
    private final UserJpaRepository userJpaRepository;

    public UserSignupResponse signup(UserSignupRequest request) {
//        boolean exists = userJpaRepository.existsByEmail(request.email());
//        if (exists) {
//        }

        return null;
    }
}
