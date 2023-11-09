package com.bobocode.svydovets.service.base;

import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.Component;

/**
 * Class for testing "autowiring" logic via field
 */
@Component
public class EditService {
    @Autowired
    private MessageService messageService;
    public String editMessage() {
        return String.format("***%s***", messageService.getMessage());
    }
}
