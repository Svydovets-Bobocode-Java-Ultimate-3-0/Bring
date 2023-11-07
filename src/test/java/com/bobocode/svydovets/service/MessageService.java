package com.bobocode.svydovets.service;

import svydovets.core.annotation.Component;

/**
 * A simple bean for "autowiring"
 */
@Component
public class MessageService {
    private String message;

    public MessageService(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
