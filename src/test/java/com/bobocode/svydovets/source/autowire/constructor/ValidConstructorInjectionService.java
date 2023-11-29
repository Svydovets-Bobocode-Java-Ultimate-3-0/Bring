package com.bobocode.svydovets.source.autowire.constructor;


import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.Component;

@Component
public class ValidConstructorInjectionService {

    private final FirstInjectionCandidate firstInjectionCandidate;
    private final SecondInjectionCandidate secondInjectionCandidate;

    @Autowired
    public ValidConstructorInjectionService(FirstInjectionCandidate firstInjectionCandidate, SecondInjectionCandidate secondInjectionCandidate) {
        this.firstInjectionCandidate = firstInjectionCandidate;
        this.secondInjectionCandidate = secondInjectionCandidate;
    }

    public FirstInjectionCandidate getFirstInjectionCandidate() {
        return firstInjectionCandidate;
    }

    public SecondInjectionCandidate getSecondInjectionCandidate() {
        return secondInjectionCandidate;
    }
}

