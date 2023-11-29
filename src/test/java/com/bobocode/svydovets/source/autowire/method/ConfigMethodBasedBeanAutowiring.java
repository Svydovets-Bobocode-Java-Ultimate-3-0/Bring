package com.bobocode.svydovets.source.autowire.method;

import com.bobocode.svydovets.source.autowire.constructor.FirstInjectionCandidate;
import com.bobocode.svydovets.source.autowire.constructor.SecondInjectionCandidate;
import com.bobocode.svydovets.source.autowire.constructor.ValidConstructorInjectionService;
import svydovets.core.annotation.Bean;
import svydovets.core.annotation.Configuration;

@Configuration
public class ConfigMethodBasedBeanAutowiring {

    @Bean
    public ValidConstructorInjectionService validConstructorInjectionService(
            FirstInjectionCandidate firstInjectionCandidate,
            SecondInjectionCandidate secondInjectionCandidate) {
        return new ValidConstructorInjectionService(firstInjectionCandidate, secondInjectionCandidate);
    }
}
