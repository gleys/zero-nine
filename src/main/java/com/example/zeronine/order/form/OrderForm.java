package com.example.zeronine.order.form;

import com.example.zeronine.category.Category;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.Map;

@Data
public class OrderForm {

    @NotBlank
    @Length(min=2, max = 30)
    private String title;

    private String description;

    private Map<Long, String> categories;

    @NotNull
    private Long categoryId;

    @NotBlank
    private String name;

    @NotNull
    private Long numberOfLimit;

    @NotNull
    private Long price;

    private String itemImage;

    @URL
    @NotBlank
    private String url;
}
