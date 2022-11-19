package com.example.zeronine.order;

import com.example.zeronine.comment.Comment;
import com.example.zeronine.item.Item;
import com.example.zeronine.settings.Keyword;
import com.example.zeronine.user.User;
import com.example.zeronine.user.UserAccount;
import lombok.*;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@EqualsAndHashCode(of = "id")
@Table(name = "orders")
@NoArgsConstructor(access = PROTECTED)
@NamedEntityGraph(name = "User.keywords", attributeNodes = @NamedAttributeNode("keywords"))
@Entity
public class Orders {

    @Id @GeneratedValue
    @Column(name = "orders_id")
    private Long id;

    @Column(nullable = false, length = 30)
    private String title;

    @Lob @Basic(fetch = LAZY)
    private String description;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "users_id", foreignKey = @ForeignKey(name = "fk_orders_to_user"))
    private User owner;

    @ManyToMany(cascade = {MERGE, PERSIST})
    private Set<User> users = new HashSet<>();

    @OneToOne(orphanRemoval = true, fetch = LAZY, cascade = {MERGE, PERSIST})
    @JoinColumn(name = "items_id", foreignKey = @ForeignKey(name = "fk_orders_to_items"))
    private Item item;

    @OneToMany(orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    private Long viewCount;

    @ManyToMany(cascade = { PERSIST, MERGE })
    @JoinTable(
            name = "orders_keywords",
            joinColumns = {@JoinColumn(name = "orders_id")},
            inverseJoinColumns = {@JoinColumn(name = "keywords_id")}
    )
    private List<Keyword> keywords = new ArrayList<>();

    private boolean closed;

    private int participantNum;

    private int numberOfLimit;

    private LocalDateTime createdAt;

    public void openOrder(User user, Item item, Integer numberOfLimit) {
        this.viewCount = 0L;
        this.setOwner(user);
        this.setItem(item);
        this.setClosed(false);
        this.participantNum = 1;
        this.numberOfLimit = numberOfLimit;
        this.createdAt = LocalDateTime.now();
    }

    public void leaveUser(User user) {
        this.users.remove(user);
        this.participantNum --;
    }

    public void participate(User user) {
        this.users.add(user);
        this.participantNum ++;
    }

    public void addKeywords(List<Keyword> keywords) {
        this.keywords.addAll(keywords);
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

    public void addViewCount() {
        this.viewCount++;
    }

    public void setClosed(boolean flag) {
        this.closed = flag;
    }

    public boolean isOwner(UserAccount user) {
        return user.getUser().equals(this.owner);
    }

    public boolean isAvailable() {
        return !closed && participantNum < numberOfLimit;
    }

    public boolean isFull() {
        return this.participantNum >= this.numberOfLimit;
    }

    public boolean isParticipant(UserAccount user) {
        return !this.owner.equals(user.getUser()) && this.users.contains(user.getUser());
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setOwner(User user) {
        this.owner = user;
        this.createdAt = LocalDateTime.now();
    }

}
