package com.example.zeronine.user.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinForm {

    @NotBlank
    @Pattern(regexp = "[ㄱ-ㅎ가-힣a-z0-9]{2,20}$")
    private String username;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Pattern(regexp = "[a-zA-Z\\\\d`~!@#$%^&*()-_=+]{8,24}$")
    private String password;
}
