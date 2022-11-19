package com.example.zeronine.order;

import com.example.zeronine.category.Category;
import com.example.zeronine.category.CategoryRepository;
import com.example.zeronine.item.Item;

import com.example.zeronine.order.event.OrderCreateEvent;
import com.example.zeronine.order.event.OrderUpdateEvent;
import com.example.zeronine.order.form.OrderForm;
import com.example.zeronine.settings.Keyword;

import com.example.zeronine.user.KeywordRepository;
import com.example.zeronine.user.User;

import com.example.zeronine.user.UserRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ModelMapper modelMapper;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final KeywordRepository keywordRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Long createOrder(User user, OrderForm form) {

        Long categoryId = form.getCategoryId();
        Category findCategory = categoryRepository.findById(categoryId).orElseThrow();

        List<Keyword> keywords = saveKeywords(form);

        Item item = modelMapper.map(form, Item.class);
        item.setCategory(findCategory);

        Orders orders = modelMapper.map(form, Orders.class);

        orders.addKeywords(keywords);
        orders.openOrder(user, item, form.getNumberOfLimit());

        orderRepository.save(orders);

        eventPublisher.publishEvent(new OrderCreateEvent(orders, keywords));

        return orders.getId();
    }

    private List<Keyword> saveKeywords(OrderForm form) {
        List<String> words = Arrays.stream(form.getKeywords().trim().replace(" ", "")
                .split(",")).collect(Collectors.toList());
        List<Keyword> existKeywords = keywordRepository.findByNames(words);

        Map<String, Keyword> keywordMap = new HashMap<>();
        existKeywords.stream().forEach(name -> keywordMap.put(name.getName(), name));

        List<Keyword> newKeywords = words.stream().filter(keyword -> keywordMap.containsKey(keyword) == false)
                .map(keyword -> Keyword.of(keyword)).collect(Collectors.toList());
        keywordRepository.saveAll(newKeywords);

        newKeywords.stream().forEach(keyword -> keywordMap.put(keyword.getName(), keyword));

        return new ArrayList<>(keywordMap.values());
    }

    public boolean participate(User user, Long id) {
        Orders findOrders = orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문번호 입니다."));
        User findUser = userRepository.findById(user.getId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다."));

        if (findOrders.isAvailable()) {
            findOrders.participate(findUser);

            if(findOrders.isFull()) {
                eventPublisher.publishEvent(new OrderUpdateEvent(findOrders, "목표한 인원수에 도달하여 주문 승인 처리 되었습니다."));
                findOrders.setClosed(true);
            }
            return true;
        }

        return false;
    }

    public boolean leave(User user, Long id) {
        Orders findOrders = orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문번호 입니다."));
        User findUser = userRepository.findById(user.getId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다."));

        if (findOrders.getUsers().contains(findUser)) {
            findOrders.leaveUser(findUser);
            return true;
        }

        return false;
    }

    public boolean update(User user, Long orderId, OrderForm form) {
        Orders findOrders = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException());

        if (findOrders.getOwner().equals(user) || !isValid(findOrders, form)) {
            List<Keyword> keywords = saveKeywords(form);

            modelMapper.map(form, findOrders);
            findOrders.setKeywords(keywords);

            Item item = findOrders.getItem();
            Category category = categoryRepository.findById(form.getCategoryId()).orElseThrow();

            modelMapper.map(form, item);
            item.setCategory(category);
            findOrders.setItem(item);

            //인원수 만원 상황에 추가로 늘려주었을 경우 상태를 open 으로 바꿈
            if(isChangeLimit(findOrders)) {
                findOrders.setClosed(false);
            }

            eventPublisher.publishEvent(new OrderUpdateEvent(findOrders, "주문 정보가 변경되었습니다."));

            return true;
        }

        return false;
    }

    private boolean isChangeLimit(Orders order) {
        return order.isClosed() && order.getNumberOfLimit() > order.getParticipantNum();
    }


    private boolean isValid(Orders orders, OrderForm form) {
        return orders.getNumberOfLimit() > form.getNumberOfLimit();
    }

    public boolean remove(User user, Long orderId) {
        Orders findOrders = orderRepository.findById(orderId).orElseThrow();
        if (findOrders.getOwner().equals(user) && !findOrders.isClosed()) {
            orderRepository.delete(findOrders);
            return true;
        }
        return false;
    }
}
