package com.taskbuddy.api.persistence.cache;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CacheManager {
    <T> Optional<T> get(@NotBlank String key, @NotNull Class<T> dataType);

    boolean hasKey(@NotBlank String key);

    boolean existsByPattern(@NotBlank String pattern);

    void save(@NotBlank String key, @NotNull Object data, @NotNull Duration timeout);

    void delete(@NotBlank String key);

    // Issue(#32) : RedisCache 패턴 검색하는 과정
    @RequiredArgsConstructor
    enum Keys {
        SIGNUP_VERIFICATION("SIGNUP:VERIFICATION", List.of("SESSION", "EMAIL", "USERNAME"), "회원가입 인증"),
        ;

        private final String prefix;
        private final List<String> argKeys;
        private final String description;

        private static final String SEP = ":";
        public static final String WILD_CARD = "*";

        public String generate(Map<String, String> argMap) {
            assert argMap.size() == this.argKeys.size() : "key 사이즈가 같아야 한다.";

            StringBuilder builder = new StringBuilder(this.prefix);
            if (CollectionUtils.isEmpty(this.argKeys)) {
                return builder.toString();
            }

            this.argKeys.forEach(argKey -> {
                assert argMap.containsKey(argKey) : "argKeys가 모두 존재해야한다.";

                builder.append(SEP)
                        .append(argKey)
                        .append(SEP)
                        .append(argMap.get(argKey));
            });

            return builder.toString();
        }

        public String pattern(Map.Entry<String, String> arg) {
            assert arg != null : "arg는 null이어서는 안된다";
            assert this.argKeys.contains(arg.getKey()) : "argKeys가 존재해야한다.";

            StringBuilder builder = new StringBuilder(this.prefix);
            if (CollectionUtils.isEmpty(this.argKeys)) {
                return builder.toString();
            }

            this.argKeys.forEach(argKey -> {
                builder.append(SEP)
                        .append(argKey)
                        .append(SEP)
                        .append(argKey.equals(arg.getKey()) ? arg.getValue() : WILD_CARD);
            });

            return builder.toString();
        }
    }
}
