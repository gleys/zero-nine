package com.example.zeronine.comment.form;

import com.example.zeronine.comment.Comment;
import com.example.zeronine.order.Order;
import com.example.zeronine.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentForm {

    private String text;
    private Long parentId;

}
