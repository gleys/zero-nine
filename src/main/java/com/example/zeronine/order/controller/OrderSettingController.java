package com.example.zeronine.order.controller;

import com.example.zeronine.category.CategoryRepository;
import com.example.zeronine.order.Order;
import com.example.zeronine.order.OrderRepository;
import com.example.zeronine.order.OrderService;
import com.example.zeronine.order.form.OrderForm;
import com.example.zeronine.user.CurrentUser;
import com.example.zeronine.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/order/setting")
@RequiredArgsConstructor
public class OrderSettingController {

    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final OrderService orderService;

    @GetMapping("/{id}")
    public String editForm(@CurrentUser User user, @PathVariable Long id, Model model) {

        Order order = orderRepository.findById(id).orElseThrow();

        if(!order.getOwner().equals(user)) {
            throw new AccessDeniedException("권한이 없는 사용자 입니다.");
        }

        Map<Long, String> categories = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(o -> o.getId(), o -> o.getName()));

        OrderForm orderForm = modelMapper.map(order, OrderForm.class);
        modelMapper.map(order.getItem(), orderForm);

        orderForm.setCategories(categories);

        model.addAttribute(orderForm);
        model.addAttribute("orderId", id);

        return "order/edit";
    }

    @PostMapping("/{orderId}")
    public String editSubmit(@CurrentUser User user, @PathVariable Long orderId,
                             @Validated OrderForm orderForm, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors() || !orderService.update(user, orderId, orderForm))  {
            Map<Long, String> categories = categoryRepository.findAll().stream()
                    .collect(Collectors.toMap(o -> o.getId(), o -> o.getName()));

            orderForm.setCategories(categories);

            model.addAttribute(user);
            model.addAttribute(orderForm);

            return "order/edit";
        }

        return "redirect:/order/" + orderId;
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity removeOrder(@CurrentUser User user, @PathVariable Long orderId) {
        log.info("remove order = {}", orderId);
        boolean remove = orderService.remove(user, orderId);

        if(!remove) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

}
