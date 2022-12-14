package com.example.zeronine.order;

import com.example.zeronine.category.Category;
import com.example.zeronine.category.repository.CategoryRepository;
import com.example.zeronine.item.Item;

import com.example.zeronine.order.event.OrderCreateEvent;
import com.example.zeronine.order.event.OrderUpdateEvent;
import com.example.zeronine.order.form.OrderForm;
import com.example.zeronine.order.respository.OrderRepository;
import com.example.zeronine.settings.Keyword;

import com.example.zeronine.user.repository.KeywordRepository;
import com.example.zeronine.user.User;

import com.example.zeronine.user.repository.UserRepository;
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

        Set<Keyword> keywords = saveKeywords(form);

        Item item = modelMapper.map(form, Item.class);
        item.setCategory(findCategory);

        Orders orders = modelMapper.map(form, Orders.class);

        orders.addKeywords(keywords);
        orders.openOrder(user, item, form.getNumberOfLimit());

        orderRepository.save(orders);

        eventPublisher.publishEvent(new OrderCreateEvent(orders, keywords));

        return orders.getId();
    }

    private Set<Keyword> saveKeywords(OrderForm form) {
        List<String> words = Arrays.stream(form.getKeywords().trim().replace(" ", "")
                .split(",")).collect(Collectors.toList());
        List<Keyword> existKeywords = keywordRepository.findByNames(words);

        Map<String, Keyword> keywordMap = new HashMap<>();
        existKeywords.stream().forEach(name -> keywordMap.put(name.getName(), name));

        List<Keyword> newKeywords = words.stream().filter(keyword -> keywordMap.containsKey(keyword) == false)
                .map(keyword -> Keyword.of(keyword)).collect(Collectors.toList());
        keywordRepository.saveAll(newKeywords);

        newKeywords.stream().forEach(keyword -> keywordMap.put(keyword.getName(), keyword));

        return new HashSet<>(keywordMap.values());
    }

    public boolean participate(User user, Long id) {
        Orders findOrders = orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("???????????? ?????? ???????????? ?????????."));
        User findUser = userRepository.findById(user.getId()).orElseThrow(() -> new IllegalArgumentException("???????????? ?????? ????????? ?????????."));

        if (findOrders.isAvailable()) {
            findOrders.participate(findUser);

            if(findOrders.isFull()) {
                eventPublisher.publishEvent(new OrderUpdateEvent(findOrders, "????????? ???????????? ???????????? ?????? ?????? ?????? ???????????????."));
                findOrders.setClosed(true);
            }
            return true;
        }

        return false;
    }

    public boolean leave(User user, Long id) {
        Orders findOrders = orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("???????????? ?????? ???????????? ?????????."));
        User findUser = userRepository.findById(user.getId()).orElseThrow(() -> new IllegalArgumentException("???????????? ?????? ????????? ?????????."));

        if (findOrders.getUsers().contains(findUser)) {
            findOrders.leaveUser(findUser);
            return true;
        }

        return false;
    }

    public boolean update(User user, Long orderId, OrderForm form) {
        Orders findOrders = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException());

        if (findOrders.getOwner().equals(user) || !isValid(findOrders, form)) {
            Set<Keyword> keywords = saveKeywords(form);

            modelMapper.map(form, findOrders);
            findOrders.setKeywords(keywords);

            Item item = findOrders.getItem();
            Category category = categoryRepository.findById(form.getCategoryId()).orElseThrow();

            modelMapper.map(form, item);
            item.setCategory(category);
            findOrders.setItem(item);

            //????????? ?????? ????????? ????????? ??????????????? ?????? ????????? open ?????? ??????
            if(isChangeLimit(findOrders)) {
                findOrders.setClosed(false);
            }

            eventPublisher.publishEvent(new OrderUpdateEvent(findOrders, "?????? ????????? ?????????????????????."));

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
