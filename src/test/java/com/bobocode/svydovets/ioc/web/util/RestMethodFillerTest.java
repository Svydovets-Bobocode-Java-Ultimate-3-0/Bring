package com.bobocode.svydovets.ioc.web.util;

import com.bobocode.svydovets.source.web.AllMethodRestController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import svydovets.web.util.RestMethodFiller;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RestMethodFillerTest {

  private RestMethodFiller filler;

  @BeforeEach
  void setUp() {
    filler = new RestMethodFiller();
  }

  @Test
  void shouldFillRestMethods() {
    Map<String, Object> beans = new HashMap<>();
    beans.put("AllMethodRestController", new AllMethodRestController());

    filler.fill(beans);

    assertEquals(1, filler.getGetMethods().size());
    assertEquals(1, filler.getPostMethods().size());
    assertEquals(1, filler.getPutMethods().size());
    assertEquals(1, filler.getDeleteMethods().size());
    assertEquals(1, filler.getPatchMethods().size());
  }
}
