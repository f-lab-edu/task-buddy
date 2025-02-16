package com.taskbuddy.api.business.user;

import com.taskbuddy.api.persistence.repository.UserJpaRepository;
import com.taskbuddy.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserJpaRepository userJpaRepository;

    public User createAndSave(UserCreate userCreate) {
        validateIfEmailAndUsernameAreUnique(userCreate.email(), userCreate.username());
        final String encodedPassword = encodePassword(userCreate.password());

        final UserEntity entity = save(userCreate.email(), userCreate.username(), encodedPassword);

        return User.from(entity);
    }

    private void validateIfEmailAndUsernameAreUnique(String email, String username) {
        Assert.notNull(email, "email must not be null");
        Assert.notNull(username, "username must not be null");

        // unique 검증 구현
    }

    private String encodePassword(String password) {
        // 단방향 암호화 (Server -> DB)

        return password;
    }

    private UserEntity save(String email, String username, String password) {
        // 필드 DB 길이 검증
        final LocalDateTime createDateTime = LocalDateTime.now();

        UserEntity entity = UserEntity.builder()
                .email(email)
                .username(username)
                .password(password)
                .passwordUpdatedAt(createDateTime)
                .createdAt(createDateTime)
                .updatedAt(createDateTime)
                .build();

//        return userJpaRepository.save(entity);
        return entity;
    }

    public Optional<User> findByUsernameAndPassword(String username, String password) {
        /**
         * username으로 유저를 조회 (존재하지 않으면 실패)
         * password가 일치하는지 확인 (라이브러리 사용)
         */

        return Optional.empty();
    }
}
