package com.bobocode.svydovets.source.autowire.constructor;

import com.bobocode.svydovets.source.base.MessageService;
import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.Component;

@Component
public class ServiceWithAutowiredConstructor {

    private final MessageService messageService;

    @Autowired
    public ServiceWithAutowiredConstructor(MessageService messageService) {
        this.messageService = messageService;
    }

    public MessageService getMessageService() {
        return messageService;
    }
}
