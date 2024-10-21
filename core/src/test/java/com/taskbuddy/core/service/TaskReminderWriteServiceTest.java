package com.taskbuddy.core.service;

import com.taskbuddy.core.database.repository.TaskReminderRepository;
import com.taskbuddy.core.domain.Task;
import com.taskbuddy.core.domain.TaskReminder;
import com.taskbuddy.core.mock.TestClockHolder;
import com.taskbuddy.core.service.port.ClockHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

class TaskReminderWriteServiceTest {
    private TaskReminderWriteService taskReminderWriteService;
    private TaskReminderRepository taskReminderRepository;
    private ClockHolder clockHolder;

    @BeforeEach
    void setUp() {
        taskReminderRepository = mock(TaskReminderRepository.class);
        clockHolder = new TestClockHolder(LocalDateTime.now());

        taskReminderWriteService = new TaskReminderWriteService(taskReminderRepository, clockHolder);
    }

    @Test
    void Task의_ReminderEnabled가_false라면_초기화를_하지_않고_종료한다() {
        //given
        Task mockTask = mock(Task.class);
        when(mockTask.isReminderEnabled()).thenReturn(false);

        //when
        taskReminderWriteService.initialize(mockTask, null);

        //then
        verify(taskReminderRepository, never()).save(any(TaskReminder.class));
    }

    @Test
    void Task의_ReminderEnabled가_true라면_주어진_interval설정으로_초기화된다() {
        //given
        Task mockTask = mock(Task.class);
        when(mockTask.isReminderEnabled()).thenReturn(true);

        Duration givenReminderInterval = Duration.ofMinutes(10);

        //when
        taskReminderWriteService.initialize(mockTask, givenReminderInterval);

        //then
        verify(taskReminderRepository, times(1)).save(any(TaskReminder.class));
    }

    @Test
    void 주어진_TaskId로_TaskReminder가_존재하지_않는경우_새로_초기화한다() {
        //given
        Task mockTask = mock(Task.class);
        when(mockTask.getId()).thenReturn(1L);
        when(taskReminderRepository.findByTaskId(mockTask.getId())).thenReturn(Optional.empty());

        Duration givenReminderInterval = Duration.ofMinutes(10);

        TaskReminderWriteService spyTaskReminderWriteService = spy(this.taskReminderWriteService);

        //when
        spyTaskReminderWriteService.update(mockTask, givenReminderInterval);

        //then
        verify(spyTaskReminderWriteService, times(1)).initialize(mockTask, givenReminderInterval);
        verify(taskReminderRepository, never()).save(any(TaskReminder.class));
        verify(taskReminderRepository, never()).deleteById(anyLong());
    }

    @Test
    void 주어진_TaskId로_TaskReminder가_존재하는데_Task의_reminder설정이_false일_경우_TaskReminder를_삭제한다() {
        //given
        Task mockTask = mock(Task.class);
        when(mockTask.getId()).thenReturn(1L);
        when(mockTask.isReminderEnabled()).thenReturn(false);

        TaskReminder mockTaskReminder = mock(TaskReminder.class);
        when(taskReminderRepository.findByTaskId(mockTask.getId())).thenReturn(Optional.of(mockTaskReminder));

        Duration givenReminderInterval = Duration.ofMinutes(10);

        //when
        taskReminderWriteService.update(mockTask, givenReminderInterval);

        //then
        verify(taskReminderRepository, times(1)).deleteById(mockTaskReminder.getId());
        verify(taskReminderRepository, never()).save(any(TaskReminder.class));
    }

    @Test
    void 주어진_TaskId로_TaskReminder가_존재하면서_Task의_reminder설정이_true일_경우_ReminderInterval을_업데이트한다() {
        //given
        Task mockTask = mock(Task.class);
        when(mockTask.getId()).thenReturn(1L);
        when(mockTask.isReminderEnabled()).thenReturn(true);

        TaskReminder mockTaskReminder = mock(TaskReminder.class);
        when(taskReminderRepository.findByTaskId(mockTask.getId())).thenReturn(Optional.of(mockTaskReminder));

        Duration givenReminderInterval = Duration.ofMinutes(10);

        //when
        taskReminderWriteService.update(mockTask, givenReminderInterval);

        //then
        verify(mockTaskReminder, times(1)).updateReminderInterval(givenReminderInterval, clockHolder);
        verify(taskReminderRepository, times(1)).save(mockTaskReminder);
    }
}
