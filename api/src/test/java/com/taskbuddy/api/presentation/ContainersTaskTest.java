package com.taskbuddy.api.presentation;

import com.taskbuddy.api.persistence.repository.TaskJpaRepository;
import com.taskbuddy.persistence.entity.TaskEntity;
import com.taskbuddy.persistence.entity.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Optional;

@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Testcontainers
@SpringBootTest
public class ContainersTaskTest {

    @Container
    private static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName(System.getenv().getOrDefault("DB_NAME", "testdb"))
            .withUsername(System.getenv().getOrDefault("DB_USERNAME", "user1"))
            .withPassword(System.getenv().getOrDefault("DB_PASSWORD", "userpass"));

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
    }

    @Autowired
    private TaskJpaRepository taskJpaRepository;

    @Test
    public void testSaveAndFindTask() {
        TaskEntity entity = TaskEntity.builder()
                .user(UserEntity.builder()
                        .id(1L)
                        .build())
                .title("title")
                .isDone(true)
                .description("description")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusHours(1))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        TaskEntity savedEntity = taskJpaRepository.save(entity);

        Optional<TaskEntity> findEntity = taskJpaRepository.findById(savedEntity.getId());

        Assertions.assertThat(findEntity).isPresent();
        Assertions.assertThat(findEntity.get().getTitle()).isEqualTo(entity.getTitle());
    }
}
