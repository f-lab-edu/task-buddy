package com.taskbuddy.api.business.taskreminder;

import com.taskbuddy.api.business.taskreminder.dto.TaskReminderInitialize;
import com.taskbuddy.api.business.taskreminder.dto.TaskReminderUpdate;
import com.taskbuddy.api.persistence.repository.TaskReminderJpaRepository;
import com.taskbuddy.persistence.entity.TaskReminderEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

class TaskReminderServiceTest {
    private TaskReminderService taskReminderService;

    private final TaskReminderJpaRepository mockTaskReminderJpaRepository = mock(TaskReminderJpaRepository.class);

    @BeforeEach
    void setUp() {
        taskReminderService = TaskReminderService.builder()
                .taskReminderJpaRepository(mockTaskReminderJpaRepository)
                .build();
    }

    @DisplayName("Reminder를 생성하여 저장할 수 있다")
    @Test
    void test_Initialize_Success() {
        //given
        Duration reminderInterval = Duration.ofMinutes(10);
        LocalDateTime requestDateTime = LocalDateTime.now();
        TaskReminderEntity entity = TaskReminderEntity.builder().build();

        TaskReminderInitialize mockTaskReminderInitialize = mock(TaskReminderInitialize.class);
        when(mockTaskReminderInitialize.createEntity(requestDateTime)).thenReturn(entity);
        when(mockTaskReminderInitialize.reminderInterval()).thenReturn(reminderInterval);

        //when
        taskReminderService.initialize(mockTaskReminderInitialize, requestDateTime);

        //then
        verify(mockTaskReminderInitialize, times(1)).createEntity(requestDateTime);
        verify(mockTaskReminderJpaRepository, times(1)).save(entity);
    }

    @DisplayName("TaskReminder가 이미 존재하면 Interval을 업데이트한다")
    @Test
    void test_Update_Success_UpdateExistingEntity() {
        //given
        TaskReminderService spyTaskReminderService = spy(taskReminderService);

        long taskId = 1;
        Duration interval = Duration.ofMinutes(20);
        LocalDateTime requestDateTime = LocalDateTime.now();

        TaskReminderUpdate mockTaskReminderUpdate = mock(TaskReminderUpdate.class);
        TaskReminderEntity findEntity = TaskReminderEntity.builder().build();
        TaskReminderEntity updatedEntity = TaskReminderEntity.builder().build();

        when(mockTaskReminderUpdate.taskId()).thenReturn(taskId);
        when(mockTaskReminderUpdate.reminderInterval()).thenReturn(interval);
        when(mockTaskReminderJpaRepository.findByTaskId(taskId)).thenReturn(Optional.of(findEntity));
        when(mockTaskReminderUpdate.updatedEntity(findEntity, requestDateTime)).thenReturn(updatedEntity);

        //when
        spyTaskReminderService.update(mockTaskReminderUpdate, requestDateTime);

        //then
        verify(mockTaskReminderJpaRepository, times(1)).findByTaskId(taskId);
        verify(mockTaskReminderUpdate, times(1)).updatedEntity(findEntity, requestDateTime);
        verify(mockTaskReminderJpaRepository, times(1)).save(updatedEntity);
        verify(spyTaskReminderService, never()).initialize(any(), any());
    }

    @DisplayName("TaskReminder가 존재하지 않는다면 초기화생성 메서드를 호출한다")
    @Test
    void test_Update_Success_InitializeNewEntity() {
        //given
        TaskReminderService spyTaskReminderService = spy(taskReminderService);

        long taskId = 1;
        Duration interval = Duration.ofMinutes(20);
        LocalDateTime requestDateTime = LocalDateTime.now();

        TaskReminderUpdate mockTaskReminderUpdate = mock(TaskReminderUpdate.class);

        when(mockTaskReminderUpdate.taskId()).thenReturn(taskId);
        when(mockTaskReminderUpdate.reminderInterval()).thenReturn(interval);
        when(mockTaskReminderJpaRepository.findByTaskId(taskId)).thenReturn(Optional.empty());

        //when
        spyTaskReminderService.update(mockTaskReminderUpdate, requestDateTime);

        //then
        verify(mockTaskReminderJpaRepository, times(1)).findByTaskId(taskId);
        verify(spyTaskReminderService, times(1)).initialize(
                eq(new TaskReminderInitialize(taskId, interval)),
                eq(requestDateTime));
    }

    @DisplayName("TaskId로 TaskReminder를 삭제할 수 있다")
    @Test
    void test_DeleteByTaskId_Success() {
        // Given
        Long taskId = 1L;
        TaskReminderEntity findEntity = mock(TaskReminderEntity.class);

        when(findEntity.getId()).thenReturn(taskId);
        when(mockTaskReminderJpaRepository.findByTaskId(taskId)).thenReturn(Optional.of(findEntity));

        // When
        taskReminderService.deleteByTaskId(taskId);

        // Then
        verify(mockTaskReminderJpaRepository, times(1)).findByTaskId(taskId);
        verify(mockTaskReminderJpaRepository, times(1)).deleteById(findEntity.getId());
    }

    @DisplayName("TaskId를 가진 TaskReminder 데이터가 존재하지 않는다면 메서드를 종료한다")
    @Test
    void test_DeleteByTaskId_EntityNotFound() {
        // Given
        Long taskId = 1L;

        when(mockTaskReminderJpaRepository.findByTaskId(taskId)).thenReturn(Optional.empty());

        // When
        taskReminderService.deleteByTaskId(taskId);

        // Then
        verify(mockTaskReminderJpaRepository, times(1)).findByTaskId(taskId);
        verify(mockTaskReminderJpaRepository, never()).deleteById(anyLong());
    }
}


