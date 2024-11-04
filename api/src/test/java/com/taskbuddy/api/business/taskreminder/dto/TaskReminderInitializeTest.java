package com.taskbuddy.api.business.taskreminder.dto;

import com.taskbuddy.persistence.entity.TaskReminderEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TaskReminderInitializeTest {

    @DisplayName("완료여부가 업데이트된_TaskEntity_인스턴스를_생성할_수_있다")
    @Test
    void test_CreateEntity_Success() {
        //given
        LocalDateTime requestDateTime = LocalDateTime.now();

        TaskReminderInitialize taskReminderInitialize = new TaskReminderInitialize(
                1L, Duration.ofMinutes(10));

        //when
        TaskReminderEntity result = taskReminderInitialize.createEntity(requestDateTime);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull();
        assertThat(result.getTask()).isNotNull();
        assertThat(result.getTask().getId()).isEqualTo(taskReminderInitialize.taskId());
        assertThat(result.getReminderInterval()).isEqualTo(taskReminderInitialize.reminderInterval());
        assertThat(result.getLastReminderSentTime()).isNull();
        assertThat(result.getCreatedAt()).isEqualTo(requestDateTime);
        assertThat(result.getUpdatedAt()).isEqualTo(requestDateTime);
    }
}
