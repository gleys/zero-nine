package com.example.zeronine.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c where c.order.id = :orderId and c.parent is not null" +
            " group by c.id, c.parent.id order by c.createdAt")
    List<Comment> getChildren(@Param("orderId") Long orderId);

    @Query("select c from Comment c where c.order.id = :orderId and c.parent is null" +
            " group by c.id, c.parent.id order by c.createdAt")
    List<Comment> getParent(@Param("orderId") Long orderId);

}
