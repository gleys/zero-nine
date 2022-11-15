package com.example.zeronine.notification;

import com.example.zeronine.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndCheckedOrderByCreatedAtDesc(User user, boolean checked);
    long countByUserAndChecked(User user, boolean checked);
    void deleteByUserAndChecked(User user, boolean checked);
}
