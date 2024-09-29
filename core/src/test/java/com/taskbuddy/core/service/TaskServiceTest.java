package com.taskbuddy.core.service;

import com.taskbuddy.core.domain.Task;
import com.taskbuddy.core.domain.TaskContentUpdate;
import com.taskbuddy.core.domain.TaskCreate;
import com.taskbuddy.core.domain.TimeFrame;
import com.taskbuddy.core.mock.TestClockHolder;
import com.taskbuddy.core.service.port.TaskRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TaskServiceTest {
    private TaskService taskService;
    private TaskRepository taskRepository;

    private LocalDateTime currentDateTime;

    @BeforeEach
    void setUp() {
        taskRepository = Mockito.mock(TaskRepository.class);
        currentDateTime = LocalDateTime.now();

        taskService = new TaskService(taskRepository, new TestClockHolder(currentDateTime));
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

        Long givenTaskId = 1L;
        Task mockTask = Task.builder()
                .id(givenTaskId)
                .title("알고리즘 문제 풀기")
                .isDone(false)
                .description("백준1902")
                .timeFrame(new TimeFrame(
                        LocalDateTime.of(2024, 8, 1, 0, 0, 0),
                        LocalDateTime.of(2024, 8, 31, 23, 59, 59)))
                .createdAt(createdDateTime)
                .updatedAt(createdDateTime)
                .build();
        Mockito.when(taskRepository.findById(givenTaskId)).thenReturn(Optional.of(mockTask));

        //when
        Task result = taskService.getTask(givenTaskId);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(givenTaskId);
        assertThat(result.getTitle()).isEqualTo(mockTask.getTitle());
        assertThat(result.getIsDone()).isEqualTo(mockTask.getIsDone());
        assertThat(result.getDescription()).isEqualTo(mockTask.getDescription());
        assertThat(result.getTimeFrame()).isEqualTo(mockTask.getTimeFrame());
        assertThat(result.getCreatedAt()).isEqualTo(mockTask.getCreatedAt());
        assertThat(result.getUpdatedAt()).isEqualTo(mockTask.getUpdatedAt());
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

        Long givenTaskId = 1L;
        Task mockTask = Task.builder()
                .id(givenTaskId)
                .title("알고리즘 문제 풀기")
                .isDone(false)
                .description("백준1902")
                .timeFrame(new TimeFrame(
                        LocalDateTime.of(2024, 8, 1, 0, 0, 0),
                        LocalDateTime.of(2024, 8, 31, 23, 59, 59)))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Mockito.when(taskRepository.save(Mockito.any())).thenReturn(mockTask);

        //when
        Long result = taskService.createTask(taskCreate);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isGreaterThan(0);
        Mockito.verify(taskRepository, Mockito.times(1)).save(Mockito.any(Task.class));
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
        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any(Task.class));
    }

    @Test
    void TaskUpdate로_Task내용을_수정할_수_있다() {
        //given
        Long givenId = 1L;
        LocalDateTime givenCreatedDateTime = LocalDateTime.now();
        boolean givenIsDone = false;
        Task mockTask = Task.builder()
                .id(givenId)
                .title("알고리즘 문제 풀기")
                .isDone(givenIsDone)
                .description("백준1902")
                .timeFrame(new TimeFrame(
                        LocalDateTime.of(2024, 8, 1, 0, 0, 0),
                        LocalDateTime.of(2024, 8, 31, 23, 59, 59)))
                .createdAt(givenCreatedDateTime)
                .build();
        Mockito.when(taskRepository.findById(givenId)).thenReturn(Optional.of(mockTask));

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
        Task findTask = taskRepository.findById(givenId).get();
        assertThat(findTask).isNotNull();
        assertThat(findTask.getId()).isEqualTo(givenId);
        assertThat(findTask.getTitle()).isEqualTo(taskContentUpdate.title());
        assertThat(findTask.getIsDone()).isEqualTo(givenIsDone);
        assertThat(findTask.getDescription()).isEqualTo(taskContentUpdate.description());
        assertThat(findTask.getTimeFrame().startDateTime()).isEqualTo(taskContentUpdate.startDateTime());
        assertThat(findTask.getTimeFrame().endDateTime()).isEqualTo(taskContentUpdate.endDateTime());
        assertThat(findTask.getCreatedAt()).isEqualTo(givenCreatedDateTime);
        assertThat(findTask.getUpdatedAt()).isEqualTo(currentDateTime);
        Mockito.verify(taskRepository, Mockito.times(1)).save(Mockito.any(Task.class));
    }

    @Test
    void Delete할_Id의_Task가_존재하지_않는다면_예외를_던진다() {
        //given
        long givenId = 1;
        Mockito.when(taskRepository.existsById(givenId)).thenReturn(false);

        //when & then
        assertThatThrownBy(() -> taskService.deleteTask(givenId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The given task with id does not exist.");
        Mockito.verify(taskRepository, Mockito.never()).deleteById(givenId);
    }

    @Test
    void 주어진ID로_Task를_삭제할_수_있다() {
        //given
        Long givenId = 1L;
        Mockito.when(taskRepository.existsById(givenId)).thenReturn(true);

        //when
        Assertions.assertDoesNotThrow(() -> taskService.deleteTask(givenId));

        //then
        Mockito.verify(taskRepository, Mockito.times(1)).deleteById(givenId);
    }
}
