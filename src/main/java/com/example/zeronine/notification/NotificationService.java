package com.example.zeronine.notification;

import com.example.zeronine.notification.respository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    public void markAsCheck(List<Notification> notifications) {
        notifications.forEach(notification -> notification.setChecked(true));
    }


}
