package com.example.zeronine.settings.validator;

import com.example.zeronine.settings.form.PasswordForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PasswordFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(PasswordForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordForm form = (PasswordForm) target;
        if (!form.getPassword().equals(form.getPasswordConfirm())) {
            errors.rejectValue("password", "wrong.value", " 비밀번호가 일치하지 않습니다.");
        }
    }
}
