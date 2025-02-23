package com.taskbuddy;

import com.taskbuddy.api.presentation.MySqlTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiApplicationTests implements MySqlTestContainer {

    @Test
    void contextLoads() {
    }

}
