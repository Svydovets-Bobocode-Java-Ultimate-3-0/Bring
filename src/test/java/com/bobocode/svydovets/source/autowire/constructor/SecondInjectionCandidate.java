package com.bobocode.svydovets.source.autowire.constructor;

import svydovets.core.annotation.Component;

@Component
public class SecondInjectionCandidate {
    public String getMessage() {
        return "I was injected";
    }
}
