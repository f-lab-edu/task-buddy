package com.taskbuddy;

import com.taskbuddy.persistence.config.MySQLEnvLoader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@TestPropertySource(locations = "classpath:application-test.yml")
@Testcontainers
@SpringBootTest
class ApiApplicationTests {
    static final String DOCKER_IMAGE = MySQLEnvLoader.get("DOCKER_IMAGE", "default-docker-image");
    static final String DB_NAME = MySQLEnvLoader.get("DB_NAME", "default-db-name");
    static final String DB_USERNAME = MySQLEnvLoader.get("DB_USERNAME", "default-username");
    static final String DB_PASSWORD = MySQLEnvLoader.get("DB_PASSWORD", "default-password");

    static DockerImageName customImage = DockerImageName.parse(DOCKER_IMAGE)
            .asCompatibleSubstituteFor("mysql");

    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(customImage)
            .withDatabaseName(DB_NAME)
            .withUsername(DB_USERNAME)
            .withPassword(DB_PASSWORD);

    @BeforeAll
    static void beforeAll() {
        mySQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mySQLContainer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @Test
    void contextLoads() {
    }
}
