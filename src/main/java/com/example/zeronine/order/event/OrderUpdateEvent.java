package com.example.zeronine.order.event;

import com.example.zeronine.order.Orders;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderUpdateEvent {
    private final Orders orders;
    private final String message;
}
