package com.example.zeronine.item.repository;

import com.example.zeronine.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
