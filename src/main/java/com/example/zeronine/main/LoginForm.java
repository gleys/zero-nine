package com.example.zeronine.main;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class LoginForm {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Pattern(regexp = "[ㄱ-ㅎ가-힣a-z0-9]{2,20}$")
    private String username;

    @NotBlank
    @Pattern(regexp = "[a-zA-Z1-9]{8,12}$")
    private String password;
}
