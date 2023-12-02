package com.bobocode.svydovets.source.circularDependency;

import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.Component;

@Component
public class FirstCircularDependencyOwner implements FirstCircularDependency {
    private SecondCircularDependency secondCircularDependency;

    @Autowired
    public FirstCircularDependencyOwner(SecondCircularDependency secondCircularDependency) {
        this.secondCircularDependency = secondCircularDependency;
    }
}
