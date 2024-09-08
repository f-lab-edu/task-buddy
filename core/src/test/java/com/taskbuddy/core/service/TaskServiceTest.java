package com.taskbuddy.core.service;

import com.taskbuddy.core.domain.Task;
import com.taskbuddy.core.domain.TaskUpdate;
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
        Task givenTask = fakeTaskRepository.save(
                Task.builder()
                        .title("알고리즘 문제 풀기")
                        .description("백준1902...")
                        .isDone(false)
                        .timeFrame(new TimeFrame(
                                LocalDateTime.of(2024, 8, 1, 0, 0, 0),
                                LocalDateTime.of(2024, 8, 31, 23, 59, 59)))
                        .build());

        //when
        Task result = taskService.getTask(givenTask.getId());

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(givenTask.getId());
    }

    @Test
    void TaskCreate로_Task를_저장할_수_있다() {
        //given

        //when

        //then
    }

    @Test
    void Update할_Id의_Task가_존재하지_않는다면_예외를_던진다() {
        //given
        long givenId = 0;
        TaskUpdate taskUpdate = new TaskUpdate(givenId, null, null, null, null, null);

        //when & then
        assertThatThrownBy(() -> taskService.updateTask(taskUpdate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The given task with id does not exist.");
    }

    @Test
    void TaskUpdate로_Task를_수정할_수_있다() {
        //given

        //when

        //then
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
