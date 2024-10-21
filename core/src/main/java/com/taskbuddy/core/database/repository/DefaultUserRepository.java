package com.taskbuddy.core.database.repository;

import com.taskbuddy.core.domain.user.UserStatus;
import com.taskbuddy.core.service.port.UserRepository;
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
