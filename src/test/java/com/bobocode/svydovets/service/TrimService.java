package com.bobocode.svydovets.service;

import com.bobocode.svydovets.service.base.EditService;
import svydovets.core.annotation.Autowired;

/**
 * Class for testing "autowiring" logic via setter. Creates as bean in the {@link BeanConfig} class
 */
public class TrimService {
    private EditService editService;

    public String trimMessage() {
        return editService.editMessage().trim();
    }

    @Autowired
    public void setEditService(EditService editService) {
        this.editService = editService;
    }
}
