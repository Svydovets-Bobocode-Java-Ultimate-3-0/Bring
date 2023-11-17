package com.bobocode.svydovets.source.collection;

import com.bobocode.svydovets.source.qualifier.withPrimary.ProductService;
import svydovets.core.annotation.Autowired;

public class CustomCollectionHolderService {

    @Autowired
    private CustomCollection<ProductService> productServiceCustomCollection;

}
