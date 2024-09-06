package com.taskbuddy.core.service;

import com.taskbuddy.core.domain.Task;
import com.taskbuddy.core.domain.TaskUpdate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TaskServiceTest {
    private TaskService taskService = new TaskService();

    @Test
    void 주어진_Id를_가진_Task가_존재하지_않으면_예외를_던진다() {
        //given
        long givenId = 0;

        //when & then
        assertThatThrownBy(() -> taskService.getTask(givenId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The given task with id does not exist.");
    }

    @Test
    void 주어진_Id를_가진_Task를_조회할_수_있다() {
        //given
        long givenId = 1;

        //when
        Task result = taskService.getTask(givenId);

        //then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(givenId);
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
