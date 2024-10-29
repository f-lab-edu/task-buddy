package com.taskbuddy.scheduler.persistence.repository;

import com.taskbuddy.scheduler.business.port.UserRepository;
import com.taskbuddy.scheduler.domain.user.UserStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class DefaultUserRepository implements UserRepository {

    @Override
    public List<Long> findAllIdsByIdsAndStatus(Set<Long> ids, UserStatus status) {
        return List.of();
    }
}
