package com.taskbuddy.scheduler.business.port;

import com.taskbuddy.scheduler.domain.user.UserStatus;

import java.util.List;
import java.util.Set;

public interface UserRepository {

    List<Long> findAllIdsByIdsAndStatus(Set<Long> ids, UserStatus status);
}
