package com.example.zeronine.settings;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static lombok.AccessLevel.PROTECTED;

@Data
@Entity
@Getter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = PROTECTED)
@Table(name = "keywords")
public class Keyword {

    @Column(name = "keyword_id")
    @Id @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public static Keyword of(String name) {
        Keyword keyword = new Keyword();
        keyword.name = name;

        return keyword;
    }
}

