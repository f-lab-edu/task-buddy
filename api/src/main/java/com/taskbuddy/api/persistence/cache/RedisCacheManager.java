package com.taskbuddy.api.persistence.cache;

import com.taskbuddy.api.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
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
    private final RedisConnectionFactory redisConnectionFactory;

    @Override
    public <T> Optional<T> get(String key, Class<T> dataType) {
        Object value = redisTemplate.opsForValue().get(key);

        return convertToInstanceOfDataType(value, dataType);
    }

    @Override
    public <T> Optional<T> getSingleValueByPattern(String pattern, Class<T> dataType) {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            ScanOptions options = ScanOptions.scanOptions().match(pattern).count(1000).build();
            Cursor<byte[]> cursor = connection.scan(options);
            while (cursor.hasNext()) {
                String key = new String(cursor.next());
                Object value = redisTemplate.opsForValue().get(key);

                return convertToInstanceOfDataType(value, dataType);
            }
        }

        return Optional.empty();
    }

    private <T> Optional<T> convertToInstanceOfDataType(Object value, Class<T> dataType) {
        if (value == null) {
            return Optional.empty();
        }

        T deserializedValue = JsonUtils.deserialize((String) value, dataType);

        return Optional.of(deserializedValue);
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
