package com.example.zeronine.order.validator;

import com.example.zeronine.config.Tokenizer;

import com.example.zeronine.order.form.OrderForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderFormValidator implements Validator {
    private final Tokenizer tokenizer;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(OrderForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        OrderForm form = (OrderForm) target;

        String title = form.getTitle();

        if(title.length() > 0 && form.getKeywords().isEmpty()) {
            String description = form.getDescription();
            String targetSentence = form.getTitle() + " " + description;

            form.setKeywords(Set.copyOf(tokenizer.getNouns(targetSentence))
                    .stream().collect(Collectors.joining(", ")));

            if(form.getKeywords().length() == 0) {
                errors.rejectValue("keywords", "wrong", "추천 키워드를 찾을 수 없습니다. 키워드를 등록해주세요.");
            }
            else {
                errors.rejectValue("keywords", "wrong", "추천 키워드 : " + form.getKeywords());
            }
        }
    }
}
