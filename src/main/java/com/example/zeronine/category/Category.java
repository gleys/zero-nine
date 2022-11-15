package com.example.zeronine.category;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@EqualsAndHashCode(of = "id")
@Table(name = "categories")
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Category {

    @Column(name = "category_id")
    @Id @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "parentCategory", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Category> childCategories = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_parent_to_children"))
    private Category parentCategory;

    public Category(String name) {
        this.name = name;
    }

    public void addChild(Category child) {
        this.childCategories.add(child);
        child.parentCategory = this;
    }

}
