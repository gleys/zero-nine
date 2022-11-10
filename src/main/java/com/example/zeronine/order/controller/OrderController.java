package com.example.zeronine.order.controller;

import com.example.zeronine.category.Category;
import com.example.zeronine.category.CategoryRepository;
import com.example.zeronine.order.Order;
import com.example.zeronine.order.OrderRepository;
import com.example.zeronine.order.OrderService;
import com.example.zeronine.order.form.OrderForm;
import com.example.zeronine.user.CurrentUser;
import com.example.zeronine.user.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Controller
public class OrderController {

    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @GetMapping("/new-order")
    public String newOrderForm(@CurrentUser User user, Model model) {
        model.addAttribute(user);
        OrderForm orderForm = new OrderForm();

        Map<Long, String> categories = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(o -> o.getId(), o -> o.getName()));

        orderForm.setCategories(categories);

        model.addAttribute(orderForm);
        model.addAttribute(user);

        return "order/form";
    }

    @PostMapping("/new-order")
    public String newOrderSubmit(@CurrentUser User user, @Validated OrderForm orderForm,
                                 BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            log.info("error occur = {}", bindingResult.getAllErrors());
            Map<Long, String> categories = categoryRepository.findAll().stream()
                    .collect(Collectors.toMap(o -> o.getId(), o -> o.getName()));

            orderForm.setCategories(categories);

            model.addAttribute(user);
            model.addAttribute(orderForm);
            return "order/form";
        }

        Long orderId = orderService.createOrder(user, orderForm);

        return "redirect:/order/" + orderId;
    }

    @GetMapping("/order/{id}")
    public String viewOrder(@CurrentUser User user, @PathVariable Long id, Model model) {
        Order order = orderRepository.findById(id).orElseThrow();

        model.addAttribute(user);
        model.addAttribute(order);

        return "order/view";
    }

    @GetMapping("/orders")
    public String myOrderList(@CurrentUser User user, Model model) {
        List<Order> orders = orderRepository.findByOwner(user.getId());
        model.addAttribute(user);
        model.addAttribute(orders);

        return "order/list";
    }

    @GetMapping("/orders/all")
    public String allOrders(@CurrentUser User user, Model model) {
        List<Order> orders = orderRepository.findAll();
        model.addAttribute(orders);
        model.addAttribute(user);

        return "order/list";
    }

    @PostMapping("/{id}/remove")
    public ResponseEntity remove(@CurrentUser User user, @PathVariable Long id) {
        boolean result = orderService.leave(user, id);
        if (!result) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/add")
    public ResponseEntity add(@CurrentUser User user, @PathVariable Long id) {
        boolean result = orderService.participate(user, id);

        if (!result) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }
}
