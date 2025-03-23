package com.taskbuddy.api.business.user;

import java.util.Optional;

public interface SigninService {

    Optional<User> findByUsernameAndPassword(String username, String password);

}
