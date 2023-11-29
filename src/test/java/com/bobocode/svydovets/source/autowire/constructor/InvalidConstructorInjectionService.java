package com.bobocode.svydovets.source.autowire.constructor;


import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.Component;

@Component
public class InvalidConstructorInjectionService {


    private FirstInjectionCandidate firstInjectionCandidate;
    private SecondInjectionCandidate secondInjectionCandidate;


    @Autowired
    public InvalidConstructorInjectionService(FirstInjectionCandidate firstInjectionCandidate, SecondInjectionCandidate secondInjectionCandidate) {
        this.firstInjectionCandidate = firstInjectionCandidate;
        this.secondInjectionCandidate = secondInjectionCandidate;
    }


    @Autowired
    public InvalidConstructorInjectionService(FirstInjectionCandidate firstInjectionCandidate) {
        this.firstInjectionCandidate = firstInjectionCandidate;
    }

    public InvalidConstructorInjectionService() {
    }
}

