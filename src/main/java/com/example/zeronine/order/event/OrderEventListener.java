package com.example.zeronine.order.event;

import com.example.zeronine.config.Tokenizer;
import com.example.zeronine.mail.EmailMessage;
import com.example.zeronine.mail.EmailService;
import com.example.zeronine.notification.Notification;
import com.example.zeronine.notification.respository.NotificationRepository;
import com.example.zeronine.order.Orders;

import com.example.zeronine.settings.Keyword;
import com.example.zeronine.user.User;
import com.example.zeronine.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Async
@Component
@Transactional
@RequiredArgsConstructor
public class OrderEventListener {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleOrderCreateEvent(OrderCreateEvent orderCreatedEvent) {
        Orders orders = orderCreatedEvent.getOrders();

        Long orderId = orders.getId();
        String title = orders.getTitle();
        Set<Keyword> keywords = orderCreatedEvent.getKeywords();

        //키워드 타겟 유저 목록 조회
        Map<String, List<User>> notificationsTarget = userRepository.findNotificationsTargetUsers(keywords);

        createNewNotification(notificationsTarget, title, orderId);

    }

    @EventListener
    public void handleOrderUpdateEvent(OrderUpdateEvent orderUpdateEvent) {
        Orders orders = orderUpdateEvent.getOrders();
        Long orderId = orders.getId();
        String message = orderUpdateEvent.getMessage();
        String subTitle = orders.getTitle().length() > 10 ? orders.getTitle().substring(0, 10).trim() + "..." : orders.getTitle();
        String title = "[주문번호 " + orders.getId()+ "]" + subTitle;

        createUpdateNotification(orders, orderId, message, title);
    }

    //주문 생성시 키워드 알람
    private void createNewNotification(Map<String, List<User>> targets, String message, Long orderId) {
        List<Notification> notifications = targets.keySet().stream()
                .map(key -> {
                    List<User> users = targets.get(key);
                    String title = "[" + key + " 키워드 알림]";
                    List<Notification> collect = users.stream().
                            map(user -> Notification.create(user, title, message, orderId))
                            .collect(Collectors.toList());
                    return collect;
                }).flatMap(o -> o.stream()).collect(Collectors.toList());

        notificationRepository.saveAll(notifications);

        sendToEmail(notifications, "제로나인 키워드 알림 : ");

    }

    //주문 정보 변경시 주문자들에게 알람
    private void createUpdateNotification(Orders orders, Long orderId, String message, String title) {
        List<Notification> notifications = orders.getUsers().stream()
                .map(user -> {
                    Notification notification = Notification.create(user, title, message, orderId);
                    return notification;
                }).collect(Collectors.toList());

        notificationRepository.saveAll(notifications);

        sendToEmail(notifications, title);
    }

    private void sendToEmail(List<Notification> notificationList, String messagePrefix) {
        List<EmailMessage> messages = notificationList.stream()
                .filter(notification -> notification.getUser().isOrderCreatedByWeb())
                .map(notification -> EmailMessage.builder()
                        .to(notification.getUser().getEmail())
                        .subject(messagePrefix + notification.getTitle())
                        .message(notification.getLink())
                        .build())
                .collect(Collectors.toList());

        messages.forEach(message -> emailService.send(message));
    }

}
