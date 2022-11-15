package com.example.zeronine.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CustomOrderRepository {

    Page<Order> findAllByPaging(Pageable pageable);
}
