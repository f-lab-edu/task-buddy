package com.taskbuddy.api.persistence.cache;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.util.Optional;

public interface CacheManager {
    <T> Optional<T> get(@NotBlank String key, @NotNull Class<T> dataType);

    boolean hasKey(@NotBlank String key);

    boolean existsByPattern(@NotBlank String pattern);

    void save(@NotBlank String key, @NotNull Object data, @NotNull Duration timeout);

    void delete(@NotBlank String key);
}
