package com.example.zeronine.item.form;

import com.example.zeronine.category.Category;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemForm {

    @NotNull
    private Category category;

    @NotBlank
    private String name;

    @NotNull
    private Long price;

    private String itemImage;

    @URL
    private String url;

}
