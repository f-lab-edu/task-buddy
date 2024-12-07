package com.taskbuddy.api.presentation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;

@TestConfiguration
public class TestContainersConfig {

    @Value("${DB_USERNAME}")
    private String username;

    @Value("${DB_PASSWORD}")
    private String password;

    @Bean
    public MySQLContainer<?> mySQLContainer() {
        MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0")
                .withDatabaseName("taskbuddy_business")
                .withUsername(username)
                .withPassword(password);

        mySQLContainer.start();
        return mySQLContainer;
    }
}
