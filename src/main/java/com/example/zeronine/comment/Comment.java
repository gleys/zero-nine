package com.example.zeronine.comment;

import com.example.zeronine.order.Orders;
import com.example.zeronine.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "comments")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = PROTECTED)
public class Comment {

    @Id @GeneratedValue
    @Column(name = "comments_id")
    private Long id;

    private String text;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "users_id", foreignKey = @ForeignKey(name = "comments_to_users"))
    private User writer;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "orders_id", foreignKey = @ForeignKey(name = "comments_to_orders"))
    private Orders orders;

    //원댓글
    @ManyToOne(fetch = LAZY, cascade = {PERSIST, MERGE})
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "children_to_parents"))
    private Comment parent;

    //대댓글
    @JsonIgnoreProperties({"parent"})
    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = ALL)
    private List<Comment> childComments = new ArrayList<>();

    private LocalDateTime createdAt;

    public static Comment write(Orders orders, User writer, Comment parent, String text) {
        Comment comment = new Comment();
        comment.writer = writer;
        comment.text = text;
        comment.orders = orders;

        //대댓글인 경우
        if (parent != null) {
            comment.parent = parent;
            parent.childComments.add(comment);
        }

        comment.createdAt = LocalDateTime.now();
        orders.getComments().add(comment);

        return comment;
    }

//    public boolean isOwner(User unknown) {
//        return this.writer.equals(unknown);
//    }

    public boolean isEditable(User unknown) {
        return childComments.size() == 0 && this.writer.equals(unknown);
    }

}
