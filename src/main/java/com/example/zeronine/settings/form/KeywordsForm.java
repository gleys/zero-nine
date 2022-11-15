package com.example.zeronine.settings.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeywordsForm {

    @Pattern(regexp = "[ㄱ-ㅎ가-힣a-z0-9]{2,10}$")
    private String keyword;
}
