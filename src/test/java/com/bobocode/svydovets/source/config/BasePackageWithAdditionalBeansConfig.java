package com.bobocode.svydovets.source.config;

import com.bobocode.svydovets.source.autowire.method.OrderService;
import com.bobocode.svydovets.source.autowire.method.TrimService;
import svydovets.core.annotation.Bean;
import svydovets.core.annotation.ComponentScan;
import svydovets.core.annotation.Configuration;

@Configuration
@ComponentScan("com.bobocode.svydovets.source.base")
public class BasePackageWithAdditionalBeansConfig {

    @Bean("megaTrimService")
    public TrimService trimService() {
        return new TrimService();
    }

    @Bean
    public OrderService orderService() {
        return new OrderService();
    }
}
