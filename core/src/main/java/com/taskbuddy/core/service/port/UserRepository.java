package com.taskbuddy.core.service.port;

import com.taskbuddy.core.domain.user.UserStatus;

import java.util.List;
import java.util.Set;

public interface UserRepository {

    List<Long> findAllIdsByIdsAndStatus(Set<Long> ids, UserStatus status);
}
