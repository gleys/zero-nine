package com.example.zeronine.order;

import com.example.zeronine.user.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface OrderRepository extends JpaRepository<Order, Long>, CustomOrderRepository {

    @Query("select o from Order o where o.owner.id = :id")
    List<Order> findByOwner(@Param("id") Long id);

    List<Order> findByUsers(User user);

    @EntityGraph("User.keywords")
    Page<Order> findAll(Pageable pageable);
}
