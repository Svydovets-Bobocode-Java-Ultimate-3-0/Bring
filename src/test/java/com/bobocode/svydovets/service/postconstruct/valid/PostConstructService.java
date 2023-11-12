package com.bobocode.svydovets.service.postconstruct.valid;

import svydovets.core.annotation.Component;
import svydovets.core.annotation.PostConstruct;

@Component
public class PostConstructService {

  private String name;

  @PostConstruct
  private void setName() {
    this.name = "post-construct-service";
  }

  public String getName() {
    return name;
  }
}
