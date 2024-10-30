package com.taskbuddy.api.business.task.dto;

import com.taskbuddy.persistence.entity.TaskEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TaskContentUpdateTest {

    @DisplayName("내용이 업데이트된_TaskEntity_인스턴스를_생성할_수_있다")
    @Test
    void test_UpdatedEntity_Success() {
        //given
        final long givenId = 1;
        LocalDateTime requestDateTime = LocalDateTime.now();
        TaskEntity entity = TaskEntity.builder()
                .id(givenId)
                .title("알고리즘 풀기")
                .isDone(false)
                .description("백준1902")
                .startDateTime(LocalDateTime.of(2024, 8, 1, 0, 0, 0))
                .endDateTime(LocalDateTime.of(2024, 8, 31, 23, 59, 59))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        TaskContentUpdate taskContentUpdate = new TaskContentUpdate(
                givenId,
                1L,
                "leetcode tree",
                "maximum depth of binary tree",
                true,
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 9, 1, 0, 0, 0),
                LocalDateTime.of(2024, 9, 10, 23, 59, 59));

        //when
        TaskEntity result = taskContentUpdate.updatedEntity(entity, requestDateTime);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isNotEqualTo(entity);
        assertThat(result.getId()).isEqualTo(givenId);
        assertThat(result.getTitle()).isEqualTo(taskContentUpdate.title());
        assertThat(result.getDescription()).isEqualTo(taskContentUpdate.description());
        assertThat(result.getIsDone()).isEqualTo(entity.getIsDone());
        assertThat(result.getStartDateTime()).isEqualTo(taskContentUpdate.startDateTime());
        assertThat(result.getEndDateTime()).isEqualTo(taskContentUpdate.endDateTime());
        assertThat(result.getCreatedAt()).isEqualTo(entity.getCreatedAt());
        assertThat(result.getUpdatedAt()).isEqualTo(requestDateTime);
    }
}
