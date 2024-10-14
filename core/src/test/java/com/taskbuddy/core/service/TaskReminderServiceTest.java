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
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TaskReminderServiceTest {
    private TaskReminderService taskReminderService;
    private TaskReminderRepository taskReminderRepository;
    private TaskService taskService;
    private UserService userService;
    private ClockHolder clockHolder;

    @BeforeEach
    void setUp() {
        taskReminderRepository = mock(TaskReminderRepository.class);
        taskService = mock(TaskService.class);
        userService = mock(UserService.class);
        clockHolder = new TestClockHolder(LocalDateTime.now());

        taskReminderService = new TaskReminderService(taskReminderRepository, taskService, userService, clockHolder);
    }

    @Test
    void Reminder를_보내야할_TaskReminder_목록을_반환한다() {
        //given
        Task mockTask1 = mock(Task.class);
        when(mockTask1.getId()).thenReturn(1L);
        when(mockTask1.getUserId()).thenReturn(1L);

        Task mockTask2 = mock(Task.class);
        when(mockTask2.getId()).thenReturn(2L);
        when(mockTask2.getUserId()).thenReturn(2L);

        List<Task> tasks = new ArrayList<>();
        tasks.add(mockTask1);
        tasks.add(mockTask2);

        when(taskService.findCurrentTasksWithReminderEnabled()).thenReturn(tasks);
        when(userService.filterUserIdsWithDisconnected(anySet())).thenReturn(Set.of(1L, 2L));

        TaskReminder mockTaskReminder1 = mock(TaskReminder.class);
        TaskReminder mockTaskReminder2 = mock(TaskReminder.class);
        when(mockTaskReminder1.isReminderDue(clockHolder)).thenReturn(true);
        when(mockTaskReminder2.isReminderDue(clockHolder)).thenReturn(false);

        when(taskReminderRepository.findAllInTaskIds(anySet())).thenReturn(Arrays.asList(mockTaskReminder1, mockTaskReminder2));

        //when
        List<TaskReminder> result = taskReminderService.getAllToSendReminder();

        //then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.contains(mockTaskReminder1)).isTrue();
        assertThat(result.contains(mockTaskReminder2)).isFalse();

        verify(taskService, times(1)).findCurrentTasksWithReminderEnabled();
        verify(userService, times(1)).filterUserIdsWithDisconnected(anySet());
        verify(taskReminderRepository, times(1)).findAllInTaskIds(anySet());
        verify(mockTaskReminder1, times(1)).isReminderDue(clockHolder);
        verify(mockTaskReminder2, times(1)).isReminderDue(clockHolder);
    }

    @Test
    void 진행중인_Task가_없다면_빈_Reminder_Task_목록이_반환된다() {
        //given
        when(taskService.findCurrentTasksWithReminderEnabled()).thenReturn(Collections.emptyList());

        //when
        List<TaskReminder> result = taskReminderService.getAllToSendReminder();

        //then
        assertThat(result.isEmpty()).isTrue();
        verify(taskService, times(1)).findCurrentTasksWithReminderEnabled();
        verify(userService, never()).filterUserIdsWithDisconnected(anySet());
    }

    @Test
    void 유저가_접속중이지_않은_Task는_Reminder_Task_목록에_포함되지_않는다() {
        //given
        Task mockTask1 = mock(Task.class);
        when(mockTask1.getId()).thenReturn(1L);
        when(mockTask1.getUserId()).thenReturn(1L);

        Task mockTask2 = mock(Task.class);
        when(mockTask2.getId()).thenReturn(2L);
        when(mockTask2.getUserId()).thenReturn(2L);

        List<Task> tasks = new ArrayList<>();
        tasks.add(mockTask1);
        tasks.add(mockTask2);

        when(taskService.findCurrentTasksWithReminderEnabled()).thenReturn(tasks);
        when(userService.filterUserIdsWithDisconnected(anySet())).thenReturn(Set.of(1L));

        TaskReminder mockTaskReminder1 = mock(TaskReminder.class);
        when(mockTaskReminder1.isReminderDue(clockHolder)).thenReturn(true);

        when(taskReminderRepository.findAllInTaskIds(Set.of(1L))).thenReturn(List.of(mockTaskReminder1));

        //when
        List<TaskReminder> result = taskReminderService.getAllToSendReminder();

        //then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.contains(mockTaskReminder1)).isTrue();

        verify(taskService, times(1)).findCurrentTasksWithReminderEnabled();
        verify(userService, times(1)).filterUserIdsWithDisconnected(Set.of(1L, 2L));
        verify(taskReminderRepository, times(1)).findAllInTaskIds(Set.of(1L));
        verify(mockTaskReminder1, times(1)).isReminderDue(clockHolder);
    }

    @Test
    void Task의_ReminderEnabled가_false라면_초기화를_하지_않고_종료한다() {
        //given
        Task mockTask = mock(Task.class);
        when(mockTask.isReminderEnabled()).thenReturn(false);

        //when
        taskReminderService.initialize(mockTask, null);

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
        taskReminderService.initialize(mockTask, givenReminderInterval);

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

        TaskReminderService spyTaskReminderService = spy(this.taskReminderService);

        //when
        spyTaskReminderService.update(mockTask, givenReminderInterval);

        //then
        verify(spyTaskReminderService, times(1)).initialize(mockTask, givenReminderInterval);
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
        taskReminderService.update(mockTask, givenReminderInterval);

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
        taskReminderService.update(mockTask, givenReminderInterval);

        //then
        verify(mockTaskReminder, times(1)).updateReminderInterval(givenReminderInterval, clockHolder);
        verify(taskReminderRepository, times(1)).save(mockTaskReminder);
    }
}
