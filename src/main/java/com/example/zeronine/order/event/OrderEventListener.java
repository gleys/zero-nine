package com.example.zeronine.order.event;

import com.example.zeronine.config.Tokenizer;
import com.example.zeronine.notification.Notification;
import com.example.zeronine.notification.NotificationRepository;
import com.example.zeronine.order.Order;

import com.example.zeronine.settings.Keyword;
import com.example.zeronine.user.User;
import com.example.zeronine.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class OrderEventListener {

    private final Tokenizer tokenizer;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleOrderCreateEvent(OrderCreateEvent orderCreatedEvent) {
        Order order = orderCreatedEvent.getOrder();

        Long orderId = order.getId();
        String title = order.getTitle();
        List<Keyword> keywords = orderCreatedEvent.getKeywords();

        //키워드 타겟 유저 목록 조회
        Map<String, List<User>> notificationsTarget = userRepository.findNotificationsTargetUsers(keywords);

        createNewNotification(notificationsTarget, title, orderId);

    }

    @EventListener
    public void handleOrderUpdateEvent(OrderUpdateEvent orderUpdateEvent) {
        Order order = orderUpdateEvent.getOrder();
        Long orderId = order.getId();
        String message = orderUpdateEvent.getMessage();
        String subTitle = order.getTitle().length() > 10 ? order.getTitle().substring(0, 10).trim() + "..." : order.getTitle();
        String title = "[주문번호 order.getId()]" + subTitle;

        createUpdateNotification(order, orderId, message, title);
    }

    //주문 생성시 키워드 알람
    private void createNewNotification(Map<String, List<User>> targets, String message, Long orderId) {
        List<Notification> result = targets.keySet().stream()
                .map(key -> {
                    List<User> users = targets.get(key);
                    String title = "[" + key + " 키워드 알림]";
                    List<Notification> collect = users.stream().
                            map(user -> Notification.create(user, title, message, orderId))
                            .collect(Collectors.toList());
                    return collect;
                }).flatMap(o -> o.stream()).collect(Collectors.toList());

        notificationRepository.saveAll(result);
    }

    //주문 정보 변경시 주문자들에게 알람
    private void createUpdateNotification(Order order, Long orderId, String message, String title) {
        List<Notification> notifications = order.getUsers().stream()
                .map(user -> {
                    Notification notification = Notification.create(user, title, message, orderId);
                    return notification;
                }).collect(Collectors.toList());

        notificationRepository.saveAll(notifications);
    }




}
