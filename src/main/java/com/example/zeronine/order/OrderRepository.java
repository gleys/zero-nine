package com.example.zeronine.order;

import com.example.zeronine.user.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o where o.owner.id = :id")
    List<Order> findByOwner(@Param("id") Long id);

    List<Order> findByUsers(User user);
}