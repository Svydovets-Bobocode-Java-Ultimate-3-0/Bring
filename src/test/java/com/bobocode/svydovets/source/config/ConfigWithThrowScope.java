package com.bobocode.svydovets.source.config;

import com.bobocode.svydovets.source.autowire.method.PopulateService;
import svydovets.core.annotation.Bean;
import svydovets.core.annotation.Configuration;
import svydovets.core.annotation.Scope;

@Configuration
public class ConfigWithThrowScope {

    @Bean
    @Scope("hernya")
    public PopulateService populateService() {
        return new PopulateService();
    }
}
