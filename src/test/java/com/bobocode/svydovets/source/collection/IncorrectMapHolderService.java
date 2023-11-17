package com.bobocode.svydovets.service.collection;

import com.bobocode.svydovets.service.qualifier.ProductService;
import svydovets.core.annotation.Autowired;

import java.util.Map;

public class IncorrectMapHolderService {

    @Autowired
    private Map<Integer, ProductService> productServiceMap;

    public Map<Integer, ProductService> getProductServiceMap() {
        return productServiceMap;
    }

    public void setProductServiceMap(Map<Integer, ProductService> productServiceMap) {
        this.productServiceMap = productServiceMap;
    }

}
