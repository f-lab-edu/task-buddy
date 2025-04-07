package com.taskbuddy.api.presentation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Import(RedisTestContainer.RedisTestConfig.class)
@Testcontainers
public interface RedisTestContainer {

    int DEFAULT_PORT = 6379;

    @Container
    GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>("redis:latest")
            .withExposedPorts(DEFAULT_PORT);

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(DEFAULT_PORT));
    }

    @TestConfiguration
    class RedisTestConfig {
        @Bean
        public LettuceConnectionFactory redisConnectionFactory(
                @Value("${spring.data.redis.host}") String host,
                @Value("${spring.data.redis.port}") int port) {
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
            return new LettuceConnectionFactory(config);
        }
    }
}
