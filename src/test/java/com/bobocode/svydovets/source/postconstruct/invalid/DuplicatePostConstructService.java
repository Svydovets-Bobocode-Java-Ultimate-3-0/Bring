package com.bobocode.svydovets.source.postconstruct.invalid;

import svydovets.core.annotation.Component;
import svydovets.core.annotation.PostConstruct;

@Component
public class DuplicatePostConstructService {

    private String name;

    @PostConstruct
    private void setFirstName() {
        this.name = "post-construct-service-1";
    }

    @PostConstruct
    private void setSecondName() {
        this.name = "post-construct-service-2";
    }

    public String getName() {
        return name;
    }

}
