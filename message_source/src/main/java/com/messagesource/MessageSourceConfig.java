package com.messagesource;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Locale;

@Configuration
public class MessageSourceConfig implements WebMvcConfigurer {

    @Bean
    public MessageSource messageSource() {
        return new YmlMessageSource();
    }
}
