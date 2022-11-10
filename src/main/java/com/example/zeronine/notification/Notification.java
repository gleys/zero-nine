package com.example.zeronine.notification;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@EqualsAndHashCode(of = "id")
@Table(name = "notifications")
public class Notification {

    @Id @GeneratedValue
    @Column(name = "noticiations_id")
    private Long id;

    private String title;

    private String link;

    private String message;

    private boolean checked;

    @ManyToOne


}
