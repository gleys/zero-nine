package com.example.zeronine.item;

import com.example.zeronine.category.Category;

import com.example.zeronine.item.form.ItemForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Data
@Getter
@EqualsAndHashCode(of = "id")
@Table(name = "items")
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Item {

    @Id @GeneratedValue
    @Column(name = "items_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "categories_id", foreignKey = @ForeignKey(name = "fk_item_to_category"))
    private Category category;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = true)
    @Lob @Basic(fetch = LAZY)
    private String itemImage;

    @Column(nullable = false)
    private String url;

    public void setCategory(Category category) {
        this.category = category;
    }

}
