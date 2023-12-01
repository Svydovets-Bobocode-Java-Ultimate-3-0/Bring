package com.bobocode.svydovets.source.qualifier.invalid;

import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.Component;
import svydovets.core.annotation.Qualifier;

@Component
public class InvalidOrderService {

    @Autowired
    @Qualifier("storeItem")
    private InvalidItem item;

    public InvalidItem getItem() {
        return item;
    }
}
