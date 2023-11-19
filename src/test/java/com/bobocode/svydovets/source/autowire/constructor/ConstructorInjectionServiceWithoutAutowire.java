package com.bobocode.svydovets.source.autowire.constructor;


import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.Component;

@Component
public class ConstructorInjectionServiceWithoutAutowire {


    private InjectionCandidate injectionCandidate;
    private InjectionCandidate2 injectionCandidate2;


    @Autowired
    public ConstructorInjectionServiceWithoutAutowire(InjectionCandidate injectionCandidate, InjectionCandidate2 injectionCandidate2) {
        this.injectionCandidate = injectionCandidate;
        this.injectionCandidate2 = injectionCandidate2;
    }


    @Autowired
    public ConstructorInjectionServiceWithoutAutowire(InjectionCandidate injectionCandidate) {
        this.injectionCandidate = injectionCandidate;
    }

    public ConstructorInjectionServiceWithoutAutowire() {
    }
}

