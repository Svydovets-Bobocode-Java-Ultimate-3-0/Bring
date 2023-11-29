package com.bobocode.svydovets.source.circularDependency;

import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.Component;

@Component
public class SecondCircularDependencyOwner implements SecondCircularDependency {
    private FirstCircularDependency firstCircularDependency;

    @Autowired
    public SecondCircularDependencyOwner(FirstCircularDependency firstCircularDependency) {
        this.firstCircularDependency = firstCircularDependency;
    }
}
