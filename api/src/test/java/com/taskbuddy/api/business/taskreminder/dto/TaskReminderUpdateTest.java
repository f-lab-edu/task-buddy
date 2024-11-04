package com.taskbuddy.api.business.taskreminder.dto;

import com.taskbuddy.persistence.entity.TaskEntity;
import com.taskbuddy.persistence.entity.TaskReminderEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TaskReminderUpdateTest {

    @DisplayName("Interval이 업데이트된 엔티티를 생성한다")
    @Test
    void test_UpdatedEntity_Success() {
        //given
        long id = 1;
        long taskId = 2;
        LocalDateTime createdDateTime = LocalDateTime.now().minusWeeks(1);
        TaskReminderEntity entity = TaskReminderEntity.builder()
                .id(id)
                .task(TaskEntity.builder()
                        .id(taskId)
                        .build())
                .reminderInterval(Duration.ofMinutes(10))
                .lastReminderSentTime(createdDateTime.minusDays(3))
                .createdAt(createdDateTime)
                .updatedAt(createdDateTime)
                .build();

        Duration expectedInterval = Duration.ofMinutes(20);
        LocalDateTime requestDateTime = LocalDateTime.now().minusWeeks(1);
        TaskReminderUpdate taskReminderUpdate = new TaskReminderUpdate(taskId, expectedInterval);

        //when
        TaskReminderEntity result = taskReminderUpdate.updatedEntity(entity, requestDateTime);

        //then
        assertThat(result).isNotEqualTo(entity);
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getTask()).isEqualTo(entity.getTask());
        assertThat(result.getReminderInterval()).isEqualTo(expectedInterval);
        assertThat(result.getLastReminderSentTime()).isEqualTo(entity.getLastReminderSentTime());
        assertThat(result.getCreatedAt()).isEqualTo(createdDateTime);
        assertThat(result.getUpdatedAt()).isEqualTo(requestDateTime);
    }

}
