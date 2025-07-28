package com.guacom.notificationservice.application.validators;

import org.springframework.stereotype.Component;

@Component
public class MessageValidator {

    public String sanitizeContent(String content) {
        return content.trim()
                .replaceAll("\\p{Cntrl}", " ")
                .replaceAll("\\s+", " ");
    }
}
