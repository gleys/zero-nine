package com.example.zeronine.order.form;

import com.example.zeronine.category.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private Integer numberOfLimit;

    @NotNull
    private Long price;

    private String itemImage;

    @NotBlank
    private String keywords;

    @URL
    @NotBlank
    private String url;
}
