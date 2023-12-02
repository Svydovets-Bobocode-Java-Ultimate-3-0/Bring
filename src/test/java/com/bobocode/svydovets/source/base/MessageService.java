package com.bobocode.svydovets.source.base;

import svydovets.core.annotation.Component;

/**
 * A simple bean for "autowiring"
 */
@Component("messageService")
public class MessageService {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
