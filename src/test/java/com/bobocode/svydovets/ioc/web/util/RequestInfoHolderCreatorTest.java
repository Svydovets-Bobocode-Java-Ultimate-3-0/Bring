package com.bobocode.svydovets.ioc.web.util;

import com.bobocode.svydovets.source.web.SimpleRestController;
import org.junit.jupiter.api.Test;
import svydovets.web.annotation.GetMapping;
import svydovets.web.dto.RequestInfoHolder;
import svydovets.web.util.RequestInfoHolderCreator;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestInfoHolderCreatorTest {

  @Test
  void shouldCreateRequestInfoHolder() {
    Method[] methods = SimpleRestController.class.getDeclaredMethods();

    RequestInfoHolder requestInfoHolder = null;

    for (Method method : methods) {
      if (method.isAnnotationPresent(GetMapping.class)) {
        requestInfoHolder =
            RequestInfoHolderCreator.create(SimpleRestController.class.getSimpleName(), method);
      }
    }

    assertEquals(SimpleRestController.class.getSimpleName(), requestInfoHolder.getClassName());
    assertEquals(1, requestInfoHolder.getParameterNames().length);
    assertEquals(1, requestInfoHolder.getParameterTypes().length);
    assertEquals("hello", requestInfoHolder.getMethodName());
  }
}
