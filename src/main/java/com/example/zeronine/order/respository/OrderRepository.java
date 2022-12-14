package com.example.zeronine.order.respository;

import com.example.zeronine.order.Orders;
import com.example.zeronine.user.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface OrderRepository extends JpaRepository<Orders, Long>, CustomOrderRepository {

    @Query("select o from Orders o where o.owner.id = :id")
    List<Orders> findByOwner(@Param("id") Long id);

    List<Orders> findByUsers(User user);

    @EntityGraph("Order.mainView")
    Page<Orders> findAll(Pageable pageable);

    @Override
    @EntityGraph("Order.view")
    Optional<Orders> findById(Long id);
}
