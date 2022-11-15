package com.example.zeronine.order.controller;

import com.example.zeronine.category.CategoryRepository;
import com.example.zeronine.comment.Comment;
import com.example.zeronine.comment.CommentRepository;
import com.example.zeronine.comment.CommentService;
import com.example.zeronine.comment.form.CommentForm;
import com.example.zeronine.config.Tokenizer;
import com.example.zeronine.order.Order;
import com.example.zeronine.order.OrderRepository;
import com.example.zeronine.order.OrderService;
import com.example.zeronine.order.form.OrderForm;
import com.example.zeronine.order.validator.OrderFormValidator;
import com.example.zeronine.user.CurrentUser;
import com.example.zeronine.user.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Controller
public class OrderController {

    private final CategoryRepository categoryRepository;
    private final CommentService commentService;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final OrderFormValidator orderFormValidator;
    private final Tokenizer tokenizer;

    @InitBinder("orderForm")
    private void orderValid(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(orderFormValidator);
    }

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
        Map<Long, Map<Long, String>> allComments = commentService.getAllComments(id);

        model.addAttribute(user);
        model.addAttribute(order);
        model.addAttribute("comments", allComments);

        model.addAttribute(new CommentForm());
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
