package com.messagesource;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequiredArgsConstructor
public class MessageSourceController {

    private final MessageSource messageSource;

    @GetMapping
    public String getMessageSourceHello(Locale locale) {
        String message = messageSource.getMessage("welcome.message", null, locale);
        return String.format("Locale: %s , Message: %s", locale, message);
    }
}
