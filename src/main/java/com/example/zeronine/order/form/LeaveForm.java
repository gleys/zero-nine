package com.example.zeronine.order.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LeaveForm {

    @NotBlank
    private String name;
}
