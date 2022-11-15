package com.example.zeronine.comment;

import com.example.zeronine.comment.form.CommentForm;
import com.example.zeronine.order.Order;
import com.example.zeronine.order.OrderRepository;
import com.example.zeronine.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final OrderRepository orderRepository;

    public void append(User user, Long orderId, CommentForm form) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문번호 입니다."));

        Comment parent = commentRepository.findById(form.getParentId()).orElse(null);
        String text = form.getText();

        commentRepository.save(Comment.write(order, user, parent, text));
    }

    public void delete(User user, Long orderId, Long commentId) {
        orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문번호 입니다."));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글 입니다."));

        if (comment.isEditable(user)) {
            commentRepository.delete(comment);
        }
    }

    public Map<Long, Map<Long, String>> getAllComments(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();

        List<Comment> parent = commentRepository.getParent(order.getId());
        List<Comment> children = commentRepository.getChildren(order.getId());

        Map<Long, Map<Long, String>> allComments = new HashMap<>();

        for (Comment comment : parent) {
            Long parentId = comment.getId();
            Map<Long, String> childComments = new HashMap<Long, String>();
            childComments.put(parentId, comment.getText());

            allComments.put(parentId, childComments);
        }

        for (Comment child : children) {
            allComments.get(child.getParent().getId()).put(child.getId(), child.getText());
        }

        return allComments;
    }



}
