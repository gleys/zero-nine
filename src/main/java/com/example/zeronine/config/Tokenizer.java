package com.example.zeronine.config;

import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL.FULL;

@Component
public class Tokenizer {

    private final Komoran tokenizer = new Komoran(FULL);


    public List<String> getNouns(String sentence) {
        KomoranResult analyzeResult = tokenizer.analyze(sentence);

        List<String> morphs = analyzeResult.getTokenList().stream()
                .filter((word) -> (word.getPos().matches("NNG|NNP")))
                .map(Token::getMorph).collect(Collectors.toList());

        return morphs;
    }
}
