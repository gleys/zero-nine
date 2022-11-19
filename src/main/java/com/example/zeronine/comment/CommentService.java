package com.example.zeronine.comment;

import com.example.zeronine.comment.form.CommentForm;
import com.example.zeronine.order.Orders;
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
        Orders orders = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문번호 입니다."));

        Comment parent = form.getParentId() != null ? commentRepository.findById(form.getParentId()).orElse(null)
                                                        : null;
        String text = form.getText();

        commentRepository.save(Comment.write(orders, user, parent, text));
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

    public Map<Comment, List<Comment>> getAllComments(Long orderId) {
        Orders orders = orderRepository.findById(orderId).orElseThrow();

        List<Comment> commentList = commentRepository.getAllByOrdersOrderByCreatedAtDesc(orders);
        Map<Comment, List<Comment>> allComments = new LinkedHashMap<>();
        List<Comment> childComments = new ArrayList<>();

        //원댓글을 키로 가지도록 맵을 생성
        commentList.forEach(comment -> {
            if(comment.getParent() == null) allComments.put(comment, new ArrayList<Comment>()); //원댓글
            else childComments.add(comment);// 대댓글
        });

        //대댓글을 알맞는 맵에 추가
        childComments.forEach(comment -> {
            allComments.get(comment.getParent()).add(comment);
        });

        //대댓글을 시간순으로 내림차순 위해 역순으로 뒤집음
        allComments.keySet().forEach(comment -> {
            List<Comment> children = allComments.get(comment);
            Collections.reverse(children);
            allComments.put(comment, children);
        });

//        //부모댓글 목록
//        List<Comment> parent = commentRepository.getParent(order.getId());
//        //자식댓글 목록
//        List<Comment> children = commentRepository.getChildren(order.getId());
//
//        Map<Comment, List<Comment>> allComments = new HashMap<>();
//
//        /*
//        * { 'parent' : [] } 모양으로 초기화
//        */
//        for (Comment comment : parent) {
//            allComments.put(comment, new ArrayList<Comment>());
//        }
//
//        /*
//         * { 'parent' : [comment1, comment2 ...] } key: 부모 Comment, value: 자식 Comments
//         */
//
//        for (Comment child : children) {
//            allComments.get(child.getParent()).add(child);
//        }
        return allComments;
    }



}
