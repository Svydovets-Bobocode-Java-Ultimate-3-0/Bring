package com.bobocode.svydovets.source.autowire.constructor;


import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.Component;

@Component
public class ConstructorInjectionService {


    private final InjectionCandidate injectionCandidate;

    @Autowired
    public ConstructorInjectionService(InjectionCandidate injectionCandidate) {
        this.injectionCandidate = injectionCandidate;
    }
}

