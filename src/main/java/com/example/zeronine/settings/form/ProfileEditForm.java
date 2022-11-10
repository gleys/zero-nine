package com.example.zeronine.settings.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ProfileEditForm {

    @Length(max = 35)
    private String bio;

    @Length(max = 50)
    private String url;

    private String profileImage;
}