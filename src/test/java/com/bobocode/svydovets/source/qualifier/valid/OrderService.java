package com.bobocode.svydovets.source.qualifier.valid;

import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.Component;
import svydovets.core.annotation.Qualifier;

@Component
public class OrderService {

    @Autowired
    @Qualifier("storeItem")
    private Item item;

    private Item secondItem;

    @Autowired
    public void setSecondItem(@Qualifier("groceryItem") Item item) {
        this.secondItem = item;
    }

    public Item getItem() {
        return item;
    }

    public Item getSecondItem() {
        return secondItem;
    }
}
