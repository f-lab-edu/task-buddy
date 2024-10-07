package com.taskbuddy.core.service;

import com.taskbuddy.core.domain.ReminderSettings;
import com.taskbuddy.core.domain.Task;
import com.taskbuddy.core.domain.User;
import com.taskbuddy.core.service.port.ClockHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ReminderNotificationService {
    private final ReminderSettingsService reminderSettingsService;
    private final UserService userService;
//    private final NotificationService notificationService;
    private final ClockHolder clockHolder;

    public void sendNotification(Task task) {
        User user = task.getUser();

        // 1. 유저가 리마인드 알림설정이 켜져있는지 확인
        if (!user.isReminderEnabled()) {
            return; // 유저가 알림을 원하지 않으면 스킵
        }

        // 2. Task의 리마인드 설정이 켜져 있는지 확인
        if (!task.isReminderEnabled()) {
            return; // Task의 리마인드 설정이 꺼져 있으면 스킵
        }

        // 3. 리마인더주기에 맞는지 확인
        ReminderSettings reminderSettings = reminderSettingsService.getByTaskId(task.getId());

        // 마지막으로 알림을 보낼 시간이 리마인드 주기 이상일 경우에만 알림 전송
        if (!reminderSettings.isReminderDue(clockHolder)) {
            return; // 아직 리마인드 주기가 지나지 않았으므로 스킵
        }

        // 4. 유저가 현재 접속 중인지 확인 (접속 중이 아니면 리마인드)
        if (!userService.isUserLoggedIn(user)) {
            //리마인더 알림 전송
//            notificationService.sendReminder(user, task);
            reminderSettingsService.updateLastSentTime(reminderSettings, LocalDateTime.now());
        }
    }
}
