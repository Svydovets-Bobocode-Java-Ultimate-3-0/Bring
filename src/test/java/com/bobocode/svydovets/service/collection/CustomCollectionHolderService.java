package com.bobocode.svydovets.service.collection;

import com.bobocode.svydovets.service.qualifier.ProductService;
import svydovets.core.annotation.Autowired;

public class CustomCollectionHolderService {

    @Autowired
    private CustomCollection<ProductService> productServiceCustomCollection;

}
