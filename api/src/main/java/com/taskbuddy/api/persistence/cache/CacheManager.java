package com.taskbuddy.api.persistence.cache;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.Optional;

public interface CacheManager {
    <T> Optional<T> get(@NotBlank String key, @NotNull Class<T> dataType);

    boolean hasKey(@NotBlank String key);

    void save(@NotBlank String key, @NotNull Object data, @NotNull Duration timeout);

    void delete(@NotBlank String key);

    @RequiredArgsConstructor
    enum Keys {
        SIGNUP_VERIFICATION("SIGNUP:VERIFICATION:EMAIL:%s", "회원가입 인증"),
        ;

        private final String format;
        private final String description;

        public String generate(Object... args) {
            return String.format(this.format, args);
        }
    }
}
