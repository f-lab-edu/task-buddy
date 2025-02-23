package com.taskbuddy;

import com.taskbuddy.api.presentation.MySqlTestContainers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiApplicationTests implements MySqlTestContainers {

    @Test
    void contextLoads() {
    }

}
