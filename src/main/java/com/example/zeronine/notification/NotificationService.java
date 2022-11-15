package com.example.zeronine.notification;

import com.example.zeronine.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void markAsCheck(List<Notification> notifications) {
        notifications.forEach(notification -> notification.setChecked(true));
    }


}
