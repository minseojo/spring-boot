package com.locale;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
public class LocaleController {

    @GetMapping
    public String getLocale(Locale locale) {
        return locale.toString();
    }

}
