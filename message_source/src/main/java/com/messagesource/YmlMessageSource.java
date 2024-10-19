package com.messagesource;

import org.springframework.context.support.AbstractMessageSource;
import org.yaml.snakeyaml.Yaml;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class YmlMessageSource extends AbstractMessageSource {

    private final Map<String, Map<String, Object>> messages = new ConcurrentHashMap<>();

    public YmlMessageSource() {
        loadYamlMessages("messages/messages_ko.yml", Locale.KOREAN);
        loadYamlMessages("messages/messages_en.yml", Locale.ENGLISH);
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        String language = locale.getLanguage();
        Map<String, Object> localeMessages = messages.get(language);
        if (localeMessages != null) {
            String message = fetchNestedMessage(code, localeMessages);
            if (message != null) {
                return new MessageFormat(message, locale);
            }
        }
        return null;
    }

    private void loadYamlMessages(String filePath, Locale locale) {
        Yaml yaml = new Yaml();
        Map<String, Object> loadedMessages = yaml.load(
                getClass().getClassLoader().getResourceAsStream(filePath));
        messages.put(locale.getLanguage(), loadedMessages);
    }

    private String fetchNestedMessage(String code, Map<String, Object> localeMessages) {
        String[] keys = code.split("\\.");
        Map<String, Object> currentMap = localeMessages;

        for (int i = 0; i < keys.length - 1; i++) {
            Object value = currentMap.get(keys[i]);
            if (value instanceof Map) {
                currentMap = (Map<String, Object>) value;
            } else {
                return null;
            }
        }

        Object finalValue = currentMap.get(keys[keys.length - 1]);
        return finalValue instanceof String ? (String) finalValue : null;
    }
}
