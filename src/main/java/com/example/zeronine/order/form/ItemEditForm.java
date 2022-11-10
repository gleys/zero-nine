package com.example.zeronine.order.form;

import com.example.zeronine.category.Category;
import lombok.Data;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Setter
public class ItemEditForm {

    @NotNull
    private Long id;

    @NotNull
    private Category category;

    @NotNull
    private List<Category> allCategories;

    @NotBlank
    private String name;

    @NotNull
    private Long price;

    private String itemImage;

    @NotNull
    private String url;
}
