package com.example.zeronine.order.controller;

import com.example.zeronine.category.repository.CategoryRepository;
import com.example.zeronine.order.Orders;
import com.example.zeronine.order.respository.OrderRepository;
import com.example.zeronine.order.OrderService;
import com.example.zeronine.order.form.OrderForm;
import com.example.zeronine.order.validator.OrderFormValidator;
import com.example.zeronine.user.security.CurrentUser;
import com.example.zeronine.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import com.example.zeronine.utils.ResponseForm.Result;
import static com.example.zeronine.utils.ResponseForm.success;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/orders/setting")
@RequiredArgsConstructor
public class OrderSettingController {

    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;
    private final OrderFormValidator orderFormValidator;
    private final ModelMapper modelMapper;
    private final OrderService orderService;

    @InitBinder("orderForm")
    private void orderValid(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(orderFormValidator);
    }


    @GetMapping("/{id}")
    public String editForm(@CurrentUser User user, @PathVariable Long id, Model model) {

        Orders orders = orderRepository.findById(id).orElseThrow();

        if(!orders.getOwner().equals(user)) {
            throw new AccessDeniedException("권한이 없는 사용자 입니다.");
        }

        Map<Long, String> categories = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(o -> o.getId(), o -> o.getName()));

        OrderForm orderForm = modelMapper.map(orders, OrderForm.class);
        modelMapper.map(orders.getItem(), orderForm);
        String keywords = orders.getKeywords().stream().map(keyword -> keyword.getName())
                                .collect(Collectors.joining(","));

        orderForm.setKeywords(keywords);

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

        return "redirect:/orders/" + orderId;
    }

    @ResponseBody
    @DeleteMapping("/{orderId}")
    public Result<String> removeOrder(@CurrentUser User user, @PathVariable Long orderId) {
        log.info("remove order = {}", orderId);
        boolean remove = orderService.remove(user, orderId);

        if(!remove) {
            return success("FAIL");
        }

        return success("OK");
    }

}
