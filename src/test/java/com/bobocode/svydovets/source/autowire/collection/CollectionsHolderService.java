package com.bobocode.svydovets.source.autowire.collection;

import com.bobocode.svydovets.source.qualifier.withPrimary.ProductService;
import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.Component;

import java.util.HashMap;
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

        @Autowired
        private Map<String, ProductService> initializedProductServiceMap = new HashMap<>();

        public Map<String, ProductService> getProductServiceMap() {
            return productServiceMap;
        }

        public void setProductServiceMap(Map<String, ProductService> productServiceMap) {
            this.productServiceMap = productServiceMap;
        }

        public Map<String, ProductService> getInitializedProductServiceMap() {
            return initializedProductServiceMap;
        }

        public void setInitializedProductServiceMap(Map<String, ProductService> initializedProductServiceMap) {
            this.initializedProductServiceMap = initializedProductServiceMap;
        }
    }
}
