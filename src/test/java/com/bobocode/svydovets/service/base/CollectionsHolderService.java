package com.bobocode.svydovets.service.base;

import com.bobocode.svydovets.service.qualifier.ProductService;
import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class CollectionsHolderService {

    @Component
    public static class ListHolderService {
        @Autowired
        private List<ProductService> productServiceList;

        public List<ProductService> getProductServiceList() {
            return productServiceList;
        }

        public void setProductServiceList(List<ProductService> productServiceList) {
            this.productServiceList = productServiceList;
        }
    }

    @Component
    public static class SetHolderService {
        @Autowired
        private Set<ProductService> productServiceSet;

        public Set<ProductService> getProductServiceSet() {
            return productServiceSet;
        }

        public void setProductServiceSet(Set<ProductService> productServiceSet) {
            this.productServiceSet = productServiceSet;
        }
    }

    @Component
    public static class MapHolderService {
        @Autowired
        private Map<String, ProductService> productServiceMap;

        public Map<String, ProductService> getProductServiceMap() {
            return productServiceMap;
        }

        public void setProductServiceMap(Map<String, ProductService> productServiceMap) {
            this.productServiceMap = productServiceMap;
        }
    }
}