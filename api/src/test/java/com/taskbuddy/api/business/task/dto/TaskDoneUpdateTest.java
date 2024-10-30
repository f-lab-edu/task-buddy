package com.taskbuddy.api.business.task.dto;

import com.taskbuddy.persistence.entity.TaskEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TaskDoneUpdateTest {

    @DisplayName("완료여부가 업데이트된_TaskEntity_인스턴스를_생성할_수_있다")
    @Test
    void test_UpdatedEntity_Success() {
        //given
        final long givenTaskId = 1;
        LocalDateTime createdDateTime = LocalDate.now().atStartOfDay().minusDays(1);
        TaskEntity originalTask = TaskEntity.builder()
                .id(givenTaskId)
                .title("create title")
                .description("create description")
                .isDone(false)
                .startDateTime(LocalDate.now().atStartOfDay())
                .endDateTime(LocalDate.now().atTime(23, 59, 59))
                .createdAt(createdDateTime)
                .updatedAt(createdDateTime)
                .build();

        TaskDoneUpdate taskDoneUpdate = new TaskDoneUpdate(givenTaskId, 2L, true);
        LocalDateTime requestDateTime = LocalDateTime.now();

        //when
        TaskEntity result = taskDoneUpdate.updatedEntity(originalTask, requestDateTime);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isNotEqualTo(originalTask);
        assertThat(result.getId()).isEqualTo(givenTaskId);
        assertThat(result.getTitle()).isEqualTo(originalTask.getTitle());
        assertThat(result.getIsDone()).isEqualTo(taskDoneUpdate.isDone());
        assertThat(result.getDescription()).isEqualTo(originalTask.getDescription());
        assertThat(result.getStartDateTime()).isEqualTo(originalTask.getStartDateTime());
        assertThat(result.getEndDateTime()).isEqualTo(originalTask.getEndDateTime());
        assertThat(result.getCreatedAt()).isEqualTo(originalTask.getCreatedAt());
        assertThat(result.getUpdatedAt()).isEqualTo(requestDateTime);
    }
}
