package com.taskbuddy.core.domain;

import com.taskbuddy.core.mock.TestClockHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TaskReminderTest {

    @Test
    void task와_reminderInterval로_TaskReminder_객체를_생성할_수_있다() {
        //given
        Task mockTask = Mockito.mock(Task.class);
        Mockito.when(mockTask.getId()).thenReturn(1L);

        Duration givenReminderInterval = Duration.ofMinutes(10);

        LocalDateTime createdDateTime = LocalDateTime.now();
        TestClockHolder testClockHolder = new TestClockHolder(createdDateTime);

        //when
        TaskReminder taskReminder = TaskReminder.from(mockTask, givenReminderInterval, testClockHolder);

        //then
        assertThat(taskReminder).isNotNull();
        assertThat(taskReminder.getId()).isNull();
        assertThat(taskReminder.getTaskId()).isEqualTo(1L);
        assertThat(taskReminder.getReminderInterval()).isEqualTo(givenReminderInterval);
        assertThat(taskReminder.getLastReminderSentTime()).isNull();;
        assertThat(taskReminder.getCreatedAt()).isEqualTo(createdDateTime);
        assertThat(taskReminder.getUpdatedAt()).isEqualTo(createdDateTime);
    }

    @Test
    void 최근발송일시를_업데이트할_수_있다() {
        //given
        LocalDateTime createdDateTime = LocalDateTime.now().minusDays(3);
        LocalDateTime lastReminderSentTime = LocalDateTime.now().minusMinutes(20);
        Duration reminderInterval = Duration.ofMinutes(20);

        TaskReminder taskReminder = TaskReminder.builder()
                .id(1L)
                .task(Task.builder().id(1L).build())
                .lastReminderSentTime(lastReminderSentTime)
                .reminderInterval(reminderInterval)
                .createdAt(createdDateTime)
                .updatedAt(createdDateTime)
                .build();

        LocalDateTime updatedLastSentTime = LocalDateTime.now();
        LocalDateTime updatedDateTime = LocalDateTime.now();

        //when
        taskReminder.updateLastReminderSentTime(updatedLastSentTime, new TestClockHolder(updatedDateTime));

        //then
        assertThat(taskReminder.getId()).isEqualTo(1L);
        assertThat(taskReminder.getTaskId()).isEqualTo(1L);
        assertThat(taskReminder.getReminderInterval()).isEqualTo(reminderInterval);
        assertThat(taskReminder.getLastReminderSentTime()).isEqualTo(updatedLastSentTime);
        assertThat(taskReminder.getCreatedAt()).isEqualTo(createdDateTime);
        assertThat(taskReminder.getUpdatedAt()).isEqualTo(updatedDateTime);
    }

    @Test
    void 현재일시가_Reminder_발송일시인지_여부를_반환한다() {
        //given
        LocalDateTime createdDateTime = LocalDateTime.now().minusDays(3);
        LocalDateTime taskStartDateTime = LocalDate.now().atStartOfDay();
        Task task = Task.builder()
                .id(1L)
                .reminderEnabled(true)
                .timeFrame(new TimeFrame(taskStartDateTime, LocalDate.now().plusWeeks(1).atStartOfDay()))
                .createdAt(createdDateTime)
                .updatedAt(createdDateTime)
                .build();

        LocalDateTime lastReminderSentTime = LocalDateTime.now().minusMinutes(20);
        Duration reminderInterval = Duration.ofMinutes(10);
        TaskReminder taskReminder = TaskReminder.builder()
                .id(1L)
                .task(task)
                .lastReminderSentTime(lastReminderSentTime)
                .reminderInterval(reminderInterval)
                .createdAt(createdDateTime)
                .updatedAt(createdDateTime)
                .build();

        //when & then
        Assertions.assertAll(
                () -> {
                    LocalDateTime currentDateTime = taskStartDateTime;
                    Assertions.assertTrue(taskReminder.isReminderDue(new TestClockHolder(currentDateTime)));
                },
                () -> {
                    LocalDateTime currentDateTime = taskStartDateTime.plusMinutes(10);
                    Assertions.assertTrue(taskReminder.isReminderDue(new TestClockHolder(currentDateTime)));
                },
                () -> {
                    LocalDateTime currentDateTime = taskStartDateTime.plusMinutes(20);
                    Assertions.assertTrue(taskReminder.isReminderDue(new TestClockHolder(currentDateTime)));
                },
                () -> {
                    LocalDateTime currentDateTime = taskStartDateTime.plusMinutes(30);
                    Assertions.assertTrue(taskReminder.isReminderDue(new TestClockHolder(currentDateTime)));
                },
                () -> {
                    LocalDateTime currentDateTime = taskStartDateTime.plusMinutes(3);
                    Assertions.assertFalse(taskReminder.isReminderDue(new TestClockHolder(currentDateTime)));
                },
                () -> {
                    LocalDateTime currentDateTime = taskStartDateTime.plusMinutes(22);
                    Assertions.assertFalse(taskReminder.isReminderDue(new TestClockHolder(currentDateTime)));
                }
        );
    }

    @Test
    void ReminderInterval을_업데이트할_수_있다() {
        //given
        LocalDateTime createdDateTime = LocalDateTime.now().minusDays(3);
        LocalDateTime lastReminderSentTime = LocalDateTime.now().minusMinutes(20);
        Duration reminderInterval = Duration.ofMinutes(20);

        TaskReminder taskReminder = TaskReminder.builder()
                .id(1L)
                .task(Task.builder().id(1L).build())
                .lastReminderSentTime(lastReminderSentTime)
                .reminderInterval(reminderInterval)
                .createdAt(createdDateTime)
                .updatedAt(createdDateTime)
                .build();

        Duration updatedReminderInterval = Duration.ofHours(1);
        LocalDateTime updatedDateTime = LocalDateTime.now();

        //when
        taskReminder.updateReminderInterval(updatedReminderInterval, new TestClockHolder(updatedDateTime));

        //then
        assertThat(taskReminder.getId()).isEqualTo(1L);
        assertThat(taskReminder.getTaskId()).isEqualTo(1L);
        assertThat(taskReminder.getReminderInterval()).isEqualTo(updatedReminderInterval);
        assertThat(taskReminder.getLastReminderSentTime()).isEqualTo(lastReminderSentTime);
        assertThat(taskReminder.getCreatedAt()).isEqualTo(createdDateTime);
        assertThat(taskReminder.getUpdatedAt()).isEqualTo(updatedDateTime);
    }
}
