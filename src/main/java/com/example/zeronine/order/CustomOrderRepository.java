package com.example.zeronine.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CustomOrderRepository {

    Page<Orders> findAllByPaging(Pageable pageable);
    Page<Orders> findByKeyword(String keyword, Pageable pageable);
}
