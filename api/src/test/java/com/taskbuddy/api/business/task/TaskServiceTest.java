package com.taskbuddy.api.business.task;

import com.taskbuddy.api.business.task.dto.TaskContentUpdate;
import com.taskbuddy.api.business.task.dto.TaskCreate;
import com.taskbuddy.api.business.task.dto.TaskDoneUpdate;
import com.taskbuddy.api.business.taskreminder.TaskReminderService;
import com.taskbuddy.api.business.taskreminder.dto.TaskReminderInitialize;
import com.taskbuddy.api.business.taskreminder.dto.TaskReminderUpdate;
import com.taskbuddy.api.persistence.repository.TaskJpaRepository;
import com.taskbuddy.api.presentation.task.response.TaskResponse;
import com.taskbuddy.persistence.entity.TaskEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class TaskServiceTest {
    private TaskService taskService;

    private final TaskReminderService mockTaskReminderService = mock(TaskReminderService.class);
    private final TaskJpaRepository mockTaskJpaRepository = mock(TaskJpaRepository.class);

    @BeforeEach
    void setUp() {
        taskService = TaskService.builder()
                .taskReminderService(mockTaskReminderService)
                .taskJpaRepository(mockTaskJpaRepository)
                .build();
    }

    @DisplayName("주어진_Id를_가진_Task가_존재하지_않으면_예외를_던진다")
    @Test
    void test_GetTaskResponse_TaskNotFound() {
        //given
        long givenId = 1;

        when(mockTaskJpaRepository.findById(givenId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> taskService.getTaskResponse(givenId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The given task with id does not exist.");
    }

    @DisplayName("주어진_Id를_가진_Task를_조회할_수_있다")
    @Test
    void test_GetTaskResponse_Success() {
        //given
        Long givenTaskId = 1L;
        TaskEntity mockTask = TaskEntity.builder()
                .id(givenTaskId)
                .title("알고리즘 문제 풀기")
                .description("백준1902")
                .startDateTime(LocalDateTime.of(2024, 8, 1, 0, 0, 0))
                .endDateTime(LocalDateTime.of(2024, 8, 31, 23, 59, 59))
                .build();
        when(mockTaskJpaRepository.findById(givenTaskId)).thenReturn(Optional.of(mockTask));

        //when
        TaskResponse result = taskService.getTaskResponse(givenTaskId);

        //then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(givenTaskId);
        assertThat(result.title()).isEqualTo(mockTask.getTitle());
        assertThat(result.isDone()).isEqualTo(mockTask.getIsDone());
        assertThat(result.description()).isEqualTo(mockTask.getDescription());
        assertThat(result.timeFrame().startDateTime()).isEqualTo(LocalDateTime.of(2024, 8, 1, 0, 0, 0));
        assertThat(result.timeFrame().endDateTime()).isEqualTo(LocalDateTime.of(2024, 8, 31, 23, 59, 59));

        verify(mockTaskJpaRepository, times(1)).findById(givenTaskId);
    }

    @DisplayName("TaskCreate로_Task를_저장할_수_있다")
    @Test
    void test_CreateTask_Success() {
        //given
        TaskCreate taskCreate = new TaskCreate(
                1L,
                "알고리즘 문제 풀기",
                "백준1902",
                false,
                null,
                LocalDateTime.of(2024, 8, 1, 0, 0, 0),
                LocalDateTime.of(2024, 8, 31, 23, 59, 59));
        LocalDateTime requestDateTime = LocalDateTime.now();

        final Long givenTaskId = 1L;
        TaskEntity mockEntity = mock(TaskEntity.class);
        when(mockEntity.getId()).thenReturn(givenTaskId);

        when(mockTaskJpaRepository.save(any())).thenReturn(mockEntity);
        doNothing().when(mockTaskReminderService).initialize(any(), any());

        //when
        Long result = taskService.createTask(taskCreate, requestDateTime);

        //then
        assertThat(result).isEqualTo(givenTaskId);
        verify(mockTaskJpaRepository, times(1)).save(any(TaskEntity.class));
        verify(mockTaskReminderService, times(1)).initialize(any(TaskReminderInitialize.class), any());
    }

    @DisplayName("Update할_Id의_Task가_존재하지_않는다면_예외를_던진다")
    @Test
    void test_UpdateContent_TaskNotFound() {
        //given
        final long givenTaskId = 0;
        TaskContentUpdate taskContentUpdate = mock(TaskContentUpdate.class);
        when(taskContentUpdate.id()).thenReturn(givenTaskId);
        when(mockTaskJpaRepository.findById(givenTaskId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> taskService.updateContent(taskContentUpdate, LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The given task with id does not exist.");
        verify(mockTaskJpaRepository, times(1)).findById(givenTaskId);
        verify(mockTaskJpaRepository, never()).save(any());
        verify(mockTaskReminderService, never()).update(any(TaskReminderUpdate.class), any());
    }

    @DisplayName("TaskUpdate로_Task내용을_수정할_수_있다")
    @Test
    void test_UpdateContent_Success() {
        //given
        final long givenTaskId = 1L;
        Duration reminderInterval = Duration.ofMinutes(10);
        TaskContentUpdate mockTaskContentUpdate = mock(TaskContentUpdate.class);
        LocalDateTime requestDateTime = LocalDateTime.now();
        TaskEntity findEntity = TaskEntity.builder().build();
        TaskEntity updatedEntity = TaskEntity.builder().build();

        when(mockTaskContentUpdate.id()).thenReturn(givenTaskId);
        when(mockTaskContentUpdate.reminderInterval()).thenReturn(reminderInterval);
        when(mockTaskJpaRepository.findById(givenTaskId)).thenReturn(Optional.of(findEntity));
        when(mockTaskContentUpdate.updatedEntity(findEntity, requestDateTime)).thenReturn(updatedEntity);

        //when
        taskService.updateContent(mockTaskContentUpdate, requestDateTime);

        //then
        verify(mockTaskJpaRepository, times(1)).findById(givenTaskId);
        verify(mockTaskContentUpdate).updatedEntity(findEntity, requestDateTime);
        verify(mockTaskJpaRepository, times(1)).save(updatedEntity);

        TaskReminderUpdate expectedTaskReminderUpdate = new TaskReminderUpdate(givenTaskId, reminderInterval);
        verify(mockTaskReminderService).update(expectedTaskReminderUpdate, requestDateTime);
    }

    @DisplayName("완료여부를_업데이트할_Task가_존재하지_않는다면_예외가_발생한다")
    @Test
    void test_UpdateDone_TaskNotFound() {
        //given
        final long givenTaskId = 1L;
        TaskDoneUpdate mockTaskDoneUpdate = mock(TaskDoneUpdate.class);
        LocalDateTime requestDateTime = LocalDateTime.now();

        when(mockTaskDoneUpdate.id()).thenReturn(givenTaskId);
        when(mockTaskJpaRepository.findById(givenTaskId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> taskService.updateDone(mockTaskDoneUpdate, requestDateTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The given task with id does not exist.");
        verify(mockTaskJpaRepository, times(1)).findById(givenTaskId);
        verify(mockTaskJpaRepository, never()).save(any());
    }

    @DisplayName("Task의 완료상태가 기존과 같다면 업데이트하지 않고 종료한다")
    @Test
    void test_UpdateDone_NoStateChange() {
        //given
        final long givenTaskId = 1L;
        TaskDoneUpdate mockTaskDoneUpdate = mock(TaskDoneUpdate.class);
        LocalDateTime requestDateTime = LocalDateTime.now();
        TaskEntity findEntity = TaskEntity.builder()
                .isDone(true)
                .build();

        when(mockTaskDoneUpdate.id()).thenReturn(givenTaskId);
        when(mockTaskDoneUpdate.isDone()).thenReturn(true);
        when(mockTaskJpaRepository.findById(givenTaskId)).thenReturn(Optional.of(findEntity));

        //when
        taskService.updateDone(mockTaskDoneUpdate, requestDateTime);

        //then
        verify(mockTaskJpaRepository, times(1)).findById(givenTaskId);
        verify(mockTaskJpaRepository, never()).save(any());
    }

    @DisplayName("Task의_완료여부를_업데이트할_수_있다")
    @Test
    void test_UpdateDone_Success() {
        //given
        final long givenTaskId = 1L;
        TaskDoneUpdate mockTaskDoneUpdate = mock(TaskDoneUpdate.class);
        LocalDateTime requestDateTime = LocalDateTime.now();
        TaskEntity findEntity = TaskEntity.builder()
                .isDone(false)
                .build();
        TaskEntity updatedEntity = TaskEntity.builder().build();

        when(mockTaskDoneUpdate.id()).thenReturn(givenTaskId);
        when(mockTaskDoneUpdate.isDone()).thenReturn(true);
        when(mockTaskJpaRepository.findById(givenTaskId)).thenReturn(Optional.of(findEntity));
        when(mockTaskDoneUpdate.updatedEntity(findEntity, requestDateTime)).thenReturn(updatedEntity);

        //when
        taskService.updateDone(mockTaskDoneUpdate, requestDateTime);

        //then
        verify(mockTaskJpaRepository, times(1)).findById(givenTaskId);
        verify(mockTaskDoneUpdate, times(1)).updatedEntity(findEntity, requestDateTime);
        verify(mockTaskJpaRepository, times(1)).save(updatedEntity);
    }

    @DisplayName("주어진 ID를 가진 Task가 존재하지 않는다면 예외를 던진다")
    @Test
    void test_DeleteTask_TaskNotExist() {
        //given
        final long givenTaskId = 1L;

        when(mockTaskJpaRepository.existsById(givenTaskId)).thenReturn(false);

        //when & then
        assertThatThrownBy(() -> taskService.deleteTask(givenTaskId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The given task with id does not exist.");

        verify(mockTaskJpaRepository, times(1)).existsById(givenTaskId);
        verify(mockTaskJpaRepository, never()).deleteById(givenTaskId);
        verify(mockTaskReminderService, never()).deleteByTaskId(givenTaskId);
    }

    @DisplayName("ID로 Task를 삭제할 수 있다.")
    @Test
    void test_DeleteTask_Success() {
        //given
        final long givenTaskId = 1L;

        when(mockTaskJpaRepository.existsById(givenTaskId)).thenReturn(true);

        //when
        taskService.deleteTask(givenTaskId);

        //then
        verify(mockTaskJpaRepository, times(1)).deleteById(givenTaskId);
        verify(mockTaskReminderService, times(1)).deleteByTaskId(givenTaskId);
    }
}
