package com.example.zeronine.settings.form;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class KeywordsForm {

    @Pattern(regexp = "[ㄱ-ㅎ가-힣a-z0-9]{2,10}$")
    private String keyword;
}
