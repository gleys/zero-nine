package com.example.zeronine.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile({"local", "test"})
public class ConsoleMailService implements EmailService {

    @Override
    public void send(EmailMessage emailMessage) {
        log.info(emailMessage.getMessage());
    }
}
