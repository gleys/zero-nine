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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ModelMapper modelMapper;
    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;
    private final KeywordRepository keywordRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final Tokenizer tokenizer;

    public Long createOrder(User user, OrderForm form) {

        Long categoryId = form.getCategoryId();
        Category findCategory = categoryRepository.findById(categoryId).orElseThrow();

        Map<String, Keyword> keywordMap = getKeywordMap(form);

        Item item = modelMapper.map(form, Item.class);
        item.setCategory(findCategory);

        Order order = modelMapper.map(form, Order.class);

        order.addKeywords(keywordMap.values().stream().collect(Collectors.toList()));
        order.openOrder(user, item);
        orderRepository.save(order);

        eventPublisher.publishEvent(new OrderCreateEvent(order, keywordMap.keySet().stream().collect(Collectors.toList())));

        return order.getId();
    }

    private Map<String, Keyword> getKeywordMap(OrderForm form) {
        Map<String, Keyword> keywordMap = Arrays.stream(form.getKeywords().trim().split(","))
                .collect(Collectors.toMap(o -> o, o -> Keyword.of(null)));

        keywordRepository.findByNames(List.copyOf(keywordMap.keySet()))
                .stream().map(keyword -> keywordMap.put(keyword.getName(), keyword));

        keywordMap.keySet().stream().filter(key -> keywordMap.get(key).getName() == null)
                .map(filtered -> keywordMap.put(filtered, Keyword.of(filtered)));

        return keywordMap;
    }

    public boolean participate(User user, Long id) {
        Order findOrder = orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());

        if (findOrder.isAvailable()) {
            findOrder.participate(user);
            return true;
        }

        return false;
    }

    public boolean leave(User user, Long id) {
        Order findOrder = orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());

        if (findOrder.getOwner().equals(user) || findOrder.getUsers().contains(user)) {
            findOrder.leaveUser(user);
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

    private List<String> recommendKeywords(String title, String description) {
        return tokenizer.getNouns(title + " " + description);
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
