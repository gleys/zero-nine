package com.example.zeronine.comment.repository;

import com.example.zeronine.comment.Comment;
import com.example.zeronine.order.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {


    // TODO: 계층형으로
    @Query("select c from Comment c where c.orders.id = :orderId and c.parent is not null" +
            " group by c.id, c.parent.id order by c.createdAt, c.writer.name desc")
    List<Comment> getChildren(@Param("orderId") Long orderId);

    @Query("select c from Comment c where c.orders.id = :orderId and c.parent is null" +
            " group by c.id, c.parent.id order by c.createdAt, c.writer.name desc")
    List<Comment> getParent(@Param("orderId") Long orderId);

    //order로 찾고나서 update 쿼리 나감 연관관계의 주인이 comment 이므로
    List<Comment> getAllByOrdersOrderByCreatedAtDesc(Orders orders);

//    with recursive all_comments(comment_id, created_at, text, users_id, orders_id, parent_id, depth) as (
//    select a.comments_id,
//    a.created_at,
//    a.text,
//    a.users_id,
//    a.orders_id,
//    a.parent_id,
//            1
//    from comments a
//    where a.parent_id is null
//    union all
//    select b.comments_id,
//    b.created_at,
//    b.text,
//    b.users_id,
//    b.orders_id,
//    b.parent_id,
//    c.depth + 1
//    from comments b, all_comments c
//    where b.parent_id = c.comment_id
//)
//    select *
//    from all_comments
//    where all_comments.orders_id = 591


}
