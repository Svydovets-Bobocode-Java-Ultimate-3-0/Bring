package com.bobocode.svydovets.source.autowire.constructor;

import svydovets.core.annotation.Component;

@Component
public class FirstInjectionCandidate {
    public String getMessage() {
        return "I was injected";
    }
}
