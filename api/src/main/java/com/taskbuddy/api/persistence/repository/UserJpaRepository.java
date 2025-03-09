package com.taskbuddy.api.persistence.repository;

import com.taskbuddy.persistence.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(@NotBlank String email);

    boolean existsByUsername(@NotBlank String username);
}
