package com.example.zeronine.settings.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UsernameForm {

    @NotBlank
    @Pattern(regexp = "[ㄱ-ㅎ가-힣a-z0-9]{2,20}$", message = "이름이 양식에 맞지 않습니다.")
    private String name;
}
