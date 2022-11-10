package com.example.zeronine.settings.validator;

import com.example.zeronine.settings.form.UsernameForm;
import com.example.zeronine.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

@Component
@RequiredArgsConstructor
public class UsernameFormValidator implements Validator {

    private final UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(UsernameForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UsernameForm form = (UsernameForm) target;
        userRepository.findByName(form.getName()).ifPresent(o -> {
                errors.rejectValue("name", "wrong.value", "입력하신 이름은 사용할 수 없습니다.");
        });
    }
}
