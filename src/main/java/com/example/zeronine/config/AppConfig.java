package com.example.zeronine.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.modelmapper.config.Configuration.AccessLevel.*;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true) //setter 없이 field에 접근가능
                .setFieldAccessLevel(PRIVATE)
                .setDestinationNameTokenizer(NameTokenizers.UNDERSCORE) //underscore 제외하고 전부 토크나이저 적용 없이 하나의 프로퍼티로 간주
                .setSourceNameTokenizer(NameTokenizers.UNDERSCORE);

        return  modelMapper;
    }

}
