package com.messagesource;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Locale;

@Configuration
public class MessageSourceConfig implements WebMvcConfigurer {

    List<Locale> supportedLocales = List.of(Locale.KOREA, Locale.KOREAN);

    @Bean
    public MessageSource messageSource() {
        XmlMessageSource xmlMessageSource = new XmlMessageSource();

        xmlMessageSource.setDefaultLocale(Locale.ENGLISH);
        for (Locale supportedLocale : supportedLocales) {
            xmlMessageSource.setMessages(supportedLocale);
        }

        return xmlMessageSource;
    }
}
