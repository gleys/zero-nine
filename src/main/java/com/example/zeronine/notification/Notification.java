package com.example.zeronine.notification;

import com.example.zeronine.user.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Slf4j
@EqualsAndHashCode(of = "id")
@Table(name = "notifications")
public class Notification {

    @Id @GeneratedValue
    @Column(name = "noticiations_id")
    private Long id;

    private String title;

    private String link;

    private String message;

    private boolean checked = false;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "users_id", foreignKey = @ForeignKey(name = "fk_users_notifications"))
    private User user;

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public static Notification create(User user, String title, String message, Long orderId) {

        Notification notification = new Notification();
        log.info("notification is created = {}", orderId);
        notification.user = user;
        notification.title = title;
        notification.message = message;
        notification.link = "/orders/" + String.valueOf(orderId);
        notification.createdAt = LocalDateTime.now();

        return notification;
    }

}
