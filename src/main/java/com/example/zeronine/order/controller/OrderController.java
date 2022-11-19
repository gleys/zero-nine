package com.example.zeronine.order.controller;

import com.example.zeronine.category.CategoryRepository;
import com.example.zeronine.comment.Comment;
import com.example.zeronine.comment.CommentService;
import com.example.zeronine.comment.form.CommentForm;

import com.example.zeronine.utils.ResponseForm.Result;
import static com.example.zeronine.utils.ResponseForm.success;

import com.example.zeronine.order.Orders;
import com.example.zeronine.order.OrderRepository;
import com.example.zeronine.order.OrderService;

import com.example.zeronine.order.form.OrderForm;
import com.example.zeronine.order.validator.OrderFormValidator;

import com.example.zeronine.user.CurrentUser;
import com.example.zeronine.user.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final CategoryRepository categoryRepository;
    private final CommentService commentService;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final OrderFormValidator orderFormValidator;


    @InitBinder("orderForm")
    private void orderValid(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(orderFormValidator);
    }

    @GetMapping("/new-order")
    public String newOrderForm(@CurrentUser User user, Model model) {
        setOrderForm(user, model, new OrderForm());

        return "order/form";
    }

    @PostMapping("/new-order")
    public String newOrderSubmit(@CurrentUser User user, @Validated OrderForm orderForm,
                                 BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            setOrderForm(user, model, orderForm);
            return "order/form";
        }

        Long orderId = orderService.createOrder(user, orderForm);
        return "redirect:/orders/" + orderId;
    }

    private void setOrderForm(User user, Model model, OrderForm orderForm) {
        Map<Long, String> categories = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(o -> o.getId(), o -> o.getName()));

        orderForm.setCategories(categories);

        model.addAttribute(user);
        model.addAttribute(orderForm);
    }

    @GetMapping("/orders/{id}")
    public String viewOrder(@CurrentUser User user, @PathVariable Long id, Model model) {
        Orders orders = orderRepository.findById(id).orElseThrow();
        orders.addViewCount();

        Map<Comment, List<Comment>> allComments = commentService.getAllComments(id);

        model.addAttribute(user);
        model.addAttribute("orders", orders);
        model.addAttribute("comments", allComments);

        model.addAttribute(new CommentForm());
        return "order/view";
    }

    @GetMapping("/orders")
    public String myOrderList(@CurrentUser User user, Model model) {
        List<Orders> orders = orderRepository.findByOwner(user.getId());
        model.addAttribute(user);
        model.addAttribute(orders);

        return "order/list";
    }

    @GetMapping("/orders/all")
    public String allOrders(@CurrentUser User user, Model model) {
        List<Orders> orders = orderRepository.findAll();
        model.addAttribute(orders);
        model.addAttribute(user);

        return "order/list";
    }

    @ResponseBody
    @PostMapping("/orders/{id}/leave")
    public Result<String> remove(@CurrentUser User user, @PathVariable Long id) {
        boolean result = orderService.leave(user, id);

        if (!result) {
            return success("FAIL");
        }

        return success("OK");
    }

    @PostMapping("/orders/{id}/join")
    public Result<String> add(@CurrentUser User user, @PathVariable Long id) {
        boolean result = orderService.participate(user, id);

        if (!result) {
            return success("FAIL");
        }

        return success("OK");
    }

}
