package com.taskbuddy.core.domain;

import com.taskbuddy.core.mock.TestClockHolder;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TaskTest {

    @Test
    void TaskCreate로_인스턴스를_생성할_수_있다() {
        //given
        LocalDateTime currentDateTime = LocalDateTime.now();
        TestClockHolder testClockHolder = new TestClockHolder(currentDateTime);

        TaskCreate taskCreate = new TaskCreate(
                1L,
                "알고리즘 풀기",
                "백준1902",
                true,
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 8, 1, 0, 0, 0),
                LocalDateTime.of(2024, 8, 31, 23, 59, 59));

        //when
        Task result = Task.from(taskCreate, testClockHolder);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull();
        assertThat(result.getTitle()).isEqualTo(taskCreate.title());
        assertThat(result.getIsDone()).isFalse();
        assertThat(result.getDescription()).isEqualTo(taskCreate.description());
        assertThat(result.getTimeFrame()).isNotNull();
        assertThat(result.getTimeFrame().startDateTime()).isEqualTo(taskCreate.startDateTime());
        assertThat(result.getTimeFrame().endDateTime()).isEqualTo(taskCreate.endDateTime());
        assertThat(result.getCreatedAt()).isEqualTo(currentDateTime);
        assertThat(result.getUpdatedAt()).isEqualTo(currentDateTime);
    }

    @Test
    void taskContentUpdate로_Task를_업데이트할_수_있다() {
        //given
        LocalDateTime currentDateTime = LocalDateTime.now();
        TestClockHolder testClockHolder = new TestClockHolder(currentDateTime);

        boolean givenIsDone = false;
        LocalDateTime createdDateTime = LocalDateTime.now().minusWeeks(1);
        Task task = Task.builder()
                .id(1L)
                .title("알고리즘 풀기")
                .isDone(givenIsDone)
                .description("백준1902")
                .timeFrame(new TimeFrame(
                        LocalDateTime.of(2024, 8, 1, 0, 0, 0),
                        LocalDateTime.of(2024, 8, 31, 23, 59, 59)
                ))
                .createdAt(createdDateTime)
                .updatedAt(createdDateTime)
                .build();

        TaskContentUpdate taskContentUpdate = new TaskContentUpdate(
                task.getId(),
                1L,
                "알고리즘 풀기",
                "백준2630",
                true,
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 9, 1, 0, 0, 0),
                LocalDateTime.of(2024, 9, 30, 23, 59, 59));

        //when
        task.update(taskContentUpdate, testClockHolder);

        //then
        assertThat(task.getId()).isEqualTo(taskContentUpdate.id());
        assertThat(task.getTitle()).isEqualTo(taskContentUpdate.title());
        assertThat(task.getIsDone()).isEqualTo(givenIsDone);
        assertThat(task.getDescription()).isEqualTo(taskContentUpdate.description());
        assertThat(task.getTimeFrame().startDateTime()).isEqualTo(taskContentUpdate.startDateTime());
        assertThat(task.getTimeFrame().endDateTime()).isEqualTo(taskContentUpdate.endDateTime());
        assertThat(task.getCreatedAt()).isEqualTo(createdDateTime);
        assertThat(task.getUpdatedAt()).isEqualTo(currentDateTime);
    }

    @Test
    void Task_완료여부가_기존값과_업데이트할_값이_같다면_업데이트하지_않고_종료한다() {
        //given
        LocalDateTime currentDateTime = LocalDateTime.now();
        TestClockHolder testClockHolder = new TestClockHolder(currentDateTime);

        boolean givenIsDone = true;
        LocalDateTime createdDateTime = LocalDateTime.now().minusWeeks(1);
        Task task = Task.builder()
                .id(1L)
                .title("알고리즘 풀기")
                .isDone(givenIsDone)
                .description("백준1902")
                .timeFrame(new TimeFrame(
                        LocalDateTime.of(2024, 8, 1, 0, 0, 0),
                        LocalDateTime.of(2024, 8, 31, 23, 59, 59)
                ))
                .createdAt(createdDateTime)
                .updatedAt(createdDateTime)
                .build();

        //when
        task.done(givenIsDone, testClockHolder);

        //then
        assertThat(task.getIsDone()).isEqualTo(givenIsDone);
        assertThat(task.getUpdatedAt()).isEqualTo(createdDateTime);
    }

    @Test
    void Task_완료여부를_업데이트할_수_있다() {
        //given
        LocalDateTime currentDateTime = LocalDateTime.now();
        TestClockHolder testClockHolder = new TestClockHolder(currentDateTime);

        boolean givenIsDoneForCreate = false;
        LocalDateTime createdDateTime = LocalDateTime.now().minusWeeks(1);
        Task task = Task.builder()
                .id(1L)
                .title("알고리즘 풀기")
                .isDone(givenIsDoneForCreate)
                .description("백준1902")
                .timeFrame(new TimeFrame(
                        LocalDateTime.of(2024, 8, 1, 0, 0, 0),
                        LocalDateTime.of(2024, 8, 31, 23, 59, 59)
                ))
                .createdAt(createdDateTime)
                .updatedAt(createdDateTime)
                .build();

        boolean givenIsDoneForUpdate = true;

        //when
        task.done(givenIsDoneForUpdate, testClockHolder);

        //then
        assertThat(task.getIsDone()).isEqualTo(givenIsDoneForUpdate);
        assertThat(task.getUpdatedAt()).isEqualTo(currentDateTime);
    }
}
