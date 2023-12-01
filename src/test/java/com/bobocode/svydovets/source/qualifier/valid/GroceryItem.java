package com.bobocode.svydovets.source.qualifier.valid;

import java.math.BigDecimal;
import svydovets.core.annotation.Component;

@Component
public class GroceryItem implements Item{
    @Override
    public BigDecimal calculatePrice() {
        return BigDecimal.ONE;
    }
}
