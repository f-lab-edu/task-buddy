package com.taskbuddy.api.persistence.repository;

import com.taskbuddy.api.business.port.UserRepository;
import com.taskbuddy.api.domain.user.UserStatus;
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
