package com.example.zeronine.settings.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordForm {

    @NotBlank
    @Pattern(regexp = "[a-zA-Z\\\\d`~!@#$%^&*()-_=+]{8,24}$")
    private String password;

    @NotBlank
    @Pattern(regexp = "[a-zA-Z\\\\d`~!@#$%^&*()-_=+]{8,24}$")
    private String passwordConfirm;

}
