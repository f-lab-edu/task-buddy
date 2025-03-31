package com.taskbuddy.api.persistence.cache;

import com.taskbuddy.api.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class RedisCacheManager implements CacheManager {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public <T> Optional<T> get(String key, Class<T> dataType) {
        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return Optional.empty();
        }

        assert dataType.isInstance(value) : "data type is incorrect";

        return Optional.of(dataType.cast(value));
    }

    @Override
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public boolean existsByPattern(String pattern) {
        Cursor<byte[]> cursor = redisTemplate.executeWithStickyConnection(redisConnection ->
                redisConnection.scan(ScanOptions.scanOptions().match(pattern).count(1000).build())
        );

        try (cursor) {
            return cursor.hasNext();
        }
    }

    @Override
    public void save(String key, Object data, Duration timeout) {
        final String serializedData = JsonUtils.serialize(data);
        redisTemplate.opsForValue().set(key, serializedData, timeout);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
