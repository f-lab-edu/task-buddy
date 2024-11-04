package com.taskbuddy.scheduler.business;

import com.taskbuddy.scheduler.business.port.UserRepository;
import com.taskbuddy.scheduler.domain.user.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public Set<Long> filterUserIdsWithOnTask(Set<Long> userIds) {
        // TODO 유저 상태를 DB에서 조회해올지 캐시에서 조회해올지 고민하기
        return new HashSet<>(userRepository.findAllIdsByIdsAndStatus(userIds, UserStatus.ON_TASK));
    }
}
