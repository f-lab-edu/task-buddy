package com.taskbuddy.core.service;

import com.taskbuddy.core.domain.Task;
import com.taskbuddy.core.domain.TaskCreate;
import com.taskbuddy.core.domain.TaskContentUpdate;
import com.taskbuddy.core.domain.TimeFrame;
import com.taskbuddy.core.mock.FakeTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TaskServiceTest {
    private TaskService taskService;
    private FakeTaskRepository fakeTaskRepository;

    private LocalDateTime currentDateTime;

    @BeforeEach
    void setUp() {
        fakeTaskRepository = new FakeTaskRepository();
        currentDateTime = LocalDateTime.now();

        taskService = new TaskService(fakeTaskRepository, () -> currentDateTime);
    }

    @Test
    void 주어진_Id를_가진_Task가_존재하지_않으면_예외를_던진다() {
        //given
        long givenId = 1;

        //when & then
        assertThatThrownBy(() -> taskService.getTask(givenId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The given task with id does not exist.");
    }

    @Test
    void 주어진_Id를_가진_Task를_조회할_수_있다() {
        //given
        LocalDateTime createdDateTime = LocalDateTime.now();
        Task givenTask = fakeTaskRepository.save(
                Task.builder()
                        .title("알고리즘 문제 풀기")
                        .isDone(false)
                        .description("백준1902")
                        .timeFrame(new TimeFrame(
                                LocalDateTime.of(2024, 8, 1, 0, 0, 0),
                                LocalDateTime.of(2024, 8, 31, 23, 59, 59)))
                        .createdAt(createdDateTime)
                        .updatedAt(createdDateTime)
                        .build());

        //when
        Task result = taskService.getTask(givenTask.getId());

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(givenTask.getId());
        assertThat(result.getTitle()).isEqualTo(givenTask.getTitle());
        assertThat(result.getIsDone()).isEqualTo(givenTask.getIsDone());
        assertThat(result.getDescription()).isEqualTo(givenTask.getDescription());
        assertThat(result.getTimeFrame()).isEqualTo(givenTask.getTimeFrame());
        assertThat(result.getCreatedAt()).isEqualTo(givenTask.getCreatedAt());
        assertThat(result.getUpdatedAt()).isEqualTo(givenTask.getUpdatedAt());
    }

    @Test
    void TaskCreate로_Task를_저장할_수_있다() {
        //given
        TaskCreate taskCreate = new TaskCreate(
                1L,
                "알고리즘 문제 풀기",
                "백준1902",
                LocalDateTime.of(2024, 8, 1, 0, 0, 0),
                LocalDateTime.of(2024, 8, 31, 23, 59, 59));

        //when
        Long result = taskService.createTask(taskCreate);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isGreaterThan(0);
    }

    @Test
    void Update할_Id의_Task가_존재하지_않는다면_예외를_던진다() {
        //given
        long givenId = 0;
        TaskContentUpdate taskContentUpdate = new TaskContentUpdate(
                givenId,
                1L,
                "알고리즘 문제 풀기",
                "백준4300",
                LocalDateTime.of(2024, 9, 1, 0, 0, 0),
                LocalDateTime.of(2024, 9, 10, 23, 59, 59));

        //when & then
        assertThatThrownBy(() -> taskService.updateContent(taskContentUpdate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The given task with id does not exist.");
    }

    @Test
    void TaskUpdate로_Task를_수정할_수_있다() {
        //given
        LocalDateTime givenCreatedDateTime = LocalDateTime.now();
        Long givenId = fakeTaskRepository.save(
                Task.builder()
                        .title("알고리즘 문제 풀기")
                        .isDone(false)
                        .description("백준1902")
                        .timeFrame(new TimeFrame(
                                LocalDateTime.of(2024, 8, 1, 0, 0, 0),
                                LocalDateTime.of(2024, 8, 31, 23, 59, 59)))
                        .createdAt(givenCreatedDateTime)
                        .build()).getId();

        TaskContentUpdate taskContentUpdate = new TaskContentUpdate(
                givenId,
                1L,
                "알고리즘 문제 풀기",
                "백준4300",
                LocalDateTime.of(2024, 9, 1, 0, 0, 0),
                LocalDateTime.of(2024, 9, 10, 23, 59, 59));

        //when
        taskService.updateContent(taskContentUpdate);

        //then
        Task findTask = fakeTaskRepository.getById(givenId);
        assertThat(findTask).isNotNull();
        assertThat(findTask.getId()).isEqualTo(givenId);
        assertThat(findTask.getTitle()).isEqualTo(taskContentUpdate.title());
//        assertThat(findTask.getIsDone()).isEqualTo(taskUpdate.isDone());
        assertThat(findTask.getDescription()).isEqualTo(taskContentUpdate.description());
        assertThat(findTask.getTimeFrame().startDateTime()).isEqualTo(taskContentUpdate.startDateTime());
        assertThat(findTask.getTimeFrame().endDateTime()).isEqualTo(taskContentUpdate.endDateTime());
        assertThat(findTask.getCreatedAt()).isEqualTo(givenCreatedDateTime);
        assertThat(findTask.getUpdatedAt()).isEqualTo(currentDateTime);
    }

    @Test
    void Delete할_Id의_Task가_존재하지_않는다면_예외를_던진다() {
        //given
        long givenId = 0;

        //when & then
        assertThatThrownBy(() -> taskService.deleteTask(givenId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The given task with id does not exist.");
    }

    @Test
    void 주어진ID로_Task를_삭제할_수_있다() {
        //given

        //when

        //then
    }
}
