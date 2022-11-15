package com.example.zeronine.order;

import com.example.zeronine.category.Category;
import com.example.zeronine.category.CategoryRepository;
import com.example.zeronine.config.Tokenizer;
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

        Order order = modelMapper.map(form, Order.class);

        order.addKeywords(keywords);
        order.openOrder(user, item, form.getNumberOfLimit());

        orderRepository.save(order);


        eventPublisher.publishEvent(new OrderCreateEvent(order, keywords));

        return order.getId();
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
        Order findOrder = orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문번호 입니다."));
        User findUser = userRepository.findById(user.getId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다."));

        if (findOrder.isAvailable()) {
            findOrder.participate(findUser);

            if(findOrder.isFull()) {
                eventPublisher.publishEvent(new OrderUpdateEvent(findOrder, "목표한 인원수에 도달하여 주문 승인 처리 되었습니다."));
                findOrder.setClosed(true);
            }
            return true;
        }

        return false;
    }

    public boolean leave(User user, Long id) {
        Order findOrder = orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문번호 입니다."));
        User findUser = userRepository.findById(user.getId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다."));

        if (findOrder.getOwner().equals(findUser) || findOrder.getUsers().contains(findUser)) {
            findOrder.leaveUser(findUser);
            return true;
        }

        return false;
    }

    public boolean update(User user, Long orderId, OrderForm form) {
        Order findOrder = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException());

        if (findOrder.getOwner().equals(user) || !isValid(findOrder, form)) {
            modelMapper.map(form, findOrder);

            Item item = findOrder.getItem();
            Category category = categoryRepository.findById(form.getCategoryId()).orElseThrow();

            modelMapper.map(form, item);
            item.setCategory(category);
            findOrder.setItem(item);

            eventPublisher.publishEvent(new OrderUpdateEvent(findOrder, "주문 정보가 변경되었습니다."));

            return true;
        }

        return false;
    }

    private boolean isValid(Order order, OrderForm form) {
        return order.getNumberOfLimit() > form.getNumberOfLimit();
    }

    public boolean remove(User user, Long orderId) {
        Order findOrder = orderRepository.findById(orderId).orElseThrow();
        if (findOrder.getOwner().equals(user) && !findOrder.isClosed()) {
            orderRepository.delete(findOrder);
            return true;
        }
        return false;
    }
}
