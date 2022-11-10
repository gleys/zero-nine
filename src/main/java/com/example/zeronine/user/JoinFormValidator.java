package com.example.zeronine.user;

import com.example.zeronine.user.form.JoinForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Slf4j
@Component
@RequiredArgsConstructor
public class JoinFormValidator implements Validator {

    private final UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(JoinForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        JoinForm form = (JoinForm) target;

        if (userRepository.existsByEmail(form.getEmail())) {
            errors.rejectValue("email", "invalid.email",
                    new Object[]{form.getEmail()}, "이미 존재하는 이메일 입니다.");
        }

        if (userRepository.existsByName(form.getUsername())) {
            errors.rejectValue("username", "invalid.userName",
                    new Object[]{form.getUsername()}, "이미 존재하는 이름 입니다.");
        }
    }
}
