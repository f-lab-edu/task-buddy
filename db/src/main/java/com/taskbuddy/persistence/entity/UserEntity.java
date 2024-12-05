package com.taskbuddy.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Entity
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private LocalDateTime passwordUpdatedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    private UserEntity(Long id, String username, String password, LocalDateTime passwordUpdatedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.passwordUpdatedAt = passwordUpdatedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UserEntityBuilder builderOfCopy() {
        return UserEntity.builder()
                .id(id)
                .username(username)
                .password(password)
                .passwordUpdatedAt(passwordUpdatedAt)
                .createdAt(createdAt)
                .updatedAt(updatedAt);
    }
}
