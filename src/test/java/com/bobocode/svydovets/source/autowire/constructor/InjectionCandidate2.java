package com.bobocode.svydovets.source.autowire.constructor;

import svydovets.core.annotation.Component;

@Component
public class InjectionCandidate2 {
    public String getMessage() {
        return "I was injected";
    }
}
