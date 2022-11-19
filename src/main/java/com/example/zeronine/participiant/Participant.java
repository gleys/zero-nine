package com.example.zeronine.participiant;

import com.example.zeronine.order.Orders;
import com.example.zeronine.user.User;

import javax.persistence.*;

import java.time.LocalDateTime;


//@Getter
//@NoArgsConstructor(access = PROTECTED)
//@Table(name = "participants")
//@Entity
public class Participant {

    @Id @GeneratedValue
    @Column(name = "participants_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "orders_id")
    private Orders orders;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private User user;

    private LocalDateTime createdAt;

}
