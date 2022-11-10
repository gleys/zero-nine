package com.example.zeronine.order;

import com.example.zeronine.category.Category;
import com.example.zeronine.category.CategoryRepository;
import com.example.zeronine.item.Item;
import com.example.zeronine.order.form.OrderForm;
import com.example.zeronine.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public Long createOrder(User user, OrderForm form) {
        log.info("form = {}", form);
        Long categoryId = form.getCategoryId();
        Category findCategory = categoryRepository.findById(categoryId).orElseThrow();

        Item item = modelMapper.map(form, Item.class);
        item.setCategory(findCategory);

        Order order = modelMapper.map(form, Order.class);
        order.openOrder(user, item);

        return orderRepository.save(order).getId();
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

            return false;
        }

        return true;
    }

    private boolean isValid(Order order, OrderForm form) {
        return order.getNumberOfLimit() > form.getNumberOfLimit();
    }
}
