package com.example.zeronine.order.event;

import com.example.zeronine.order.Order;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderUpdateEvent {
    private final Order order;
    private final String message;
}
