package com.example.zeronine.order.event;

import com.example.zeronine.order.Order;
import com.example.zeronine.settings.Keyword;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class OrderCreateEvent {

    private final Order order;
    private final List<Keyword> keywords;

}
