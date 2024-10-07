package com.taskbuddy.core.service;

import com.taskbuddy.core.database.repository.ReminderSettingsRepository;
import com.taskbuddy.core.domain.ReminderSettings;
import com.taskbuddy.core.domain.Task;
import com.taskbuddy.core.mock.TestClockHolder;
import com.taskbuddy.core.service.port.ClockHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

class ReminderSettingsServiceTest {
    private ReminderSettingsService reminderSettingsService;
    private ReminderSettingsRepository reminderSettingsRepository;
    private ClockHolder clockHolder;

    @BeforeEach
    void setUp() {
        reminderSettingsRepository = mock(ReminderSettingsRepository.class);
        clockHolder = new TestClockHolder(LocalDateTime.now());

        reminderSettingsService = new ReminderSettingsService(reminderSettingsRepository, clockHolder);
    }

    @Test
    void Task의_ReminderEnabled가_false라면_초기화를_하지_않고_종료한다() {
        //given
        Task mockTask = mock(Task.class);
        when(mockTask.isReminderEnabled()).thenReturn(false);

        //when
        reminderSettingsService.initialize(mockTask, null);

        //then
        verify(reminderSettingsRepository, never()).save(any(ReminderSettings.class));
    }

    @Test
    void Task의_ReminderEnabled가_true라면_주어진_interval설정으로_초기화된다() {
        //given
        Task mockTask = mock(Task.class);
        when(mockTask.isReminderEnabled()).thenReturn(true);

        Duration givenReminderInterval = Duration.ofMinutes(10);

        //when
        reminderSettingsService.initialize(mockTask, givenReminderInterval);

        //then
        verify(reminderSettingsRepository, times(1)).save(any(ReminderSettings.class));
    }

    @Test
    void 주어진_TaskId로_ReminderSettings가_존재하지_않는경우_새로_초기화한다() {
        //given
        Task mockTask = mock(Task.class);
        when(mockTask.getId()).thenReturn(1L);
        when(reminderSettingsRepository.findByTaskId(mockTask.getId())).thenReturn(Optional.empty());

        Duration givenReminderInterval = Duration.ofMinutes(10);

        ReminderSettingsService spyReminderSettingsService = spy(this.reminderSettingsService);

        //when
        spyReminderSettingsService.update(mockTask, givenReminderInterval);

        //then
        verify(spyReminderSettingsService, times(1)).initialize(mockTask, givenReminderInterval);
        verify(reminderSettingsRepository, never()).save(any(ReminderSettings.class));
        verify(reminderSettingsRepository, never()).deleteById(anyLong());
    }

    @Test
    void 주어진_TaskId로_ReminderSettings가_존재하는데_Task의_reminder설정이_false일_경우_ReminderSettings를_삭제한다() {
        //given
        Task mockTask = mock(Task.class);
        when(mockTask.getId()).thenReturn(1L);
        when(mockTask.isReminderEnabled()).thenReturn(false);

        ReminderSettings mockReminderSettings = mock(ReminderSettings.class);
        when(reminderSettingsRepository.findByTaskId(mockTask.getId())).thenReturn(Optional.of(mockReminderSettings));

        Duration givenReminderInterval = Duration.ofMinutes(10);

        //when
        reminderSettingsService.update(mockTask, givenReminderInterval);

        //then
        verify(reminderSettingsRepository, times(1)).deleteById(mockReminderSettings.getId());
        verify(reminderSettingsRepository, never()).save(any(ReminderSettings.class));
    }

    @Test
    void 주어진_TaskId로_ReminderSettings가_존재하면서_Task의_reminder설정이_true일_경우_ReminderInterval을_업데이트한다() {
        //given
        Task mockTask = mock(Task.class);
        when(mockTask.getId()).thenReturn(1L);
        when(mockTask.isReminderEnabled()).thenReturn(true);

        ReminderSettings mockReminderSettings = mock(ReminderSettings.class);
        when(reminderSettingsRepository.findByTaskId(mockTask.getId())).thenReturn(Optional.of(mockReminderSettings));

        Duration givenReminderInterval = Duration.ofMinutes(10);

        //when
        reminderSettingsService.update(mockTask, givenReminderInterval);

        //then
        verify(mockReminderSettings, times(1)).updateReminderInterval(givenReminderInterval, clockHolder);
        verify(reminderSettingsRepository, times(1)).save(mockReminderSettings);
    }
}
