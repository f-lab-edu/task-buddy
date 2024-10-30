package com.taskbuddy.api.business.task.dto;

import com.taskbuddy.persistence.entity.TaskEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TaskCreateTest {

    @DisplayName("TaskEntity_인스턴스를_생성할_수_있다")
    @Test
    void test_CreateEntity_Success() {
        //given
        LocalDateTime currentDateTime = LocalDateTime.now();

        TaskCreate taskCreate = new TaskCreate(
                1L,
                "알고리즘 풀기",
                "백준1902",
                true,
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 8, 1, 0, 0, 0),
                LocalDateTime.of(2024, 8, 31, 23, 59, 59));

        //when
        TaskEntity result = taskCreate.createEntity(currentDateTime);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull();
        assertThat(result.getTitle()).isEqualTo(taskCreate.title());
        assertThat(result.getIsDone()).isFalse();
        assertThat(result.getDescription()).isEqualTo(taskCreate.description());
        assertThat(result.getStartDateTime()).isEqualTo(taskCreate.startDateTime());
        assertThat(result.getEndDateTime()).isEqualTo(taskCreate.endDateTime());
        assertThat(result.getCreatedAt()).isEqualTo(currentDateTime);
        assertThat(result.getUpdatedAt()).isEqualTo(currentDateTime);
    }
}
