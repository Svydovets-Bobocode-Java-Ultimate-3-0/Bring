package com.bobocode.svydovets.ioc.web.util;

import com.bobocode.svydovets.source.web.SimpleRestController;
import org.junit.jupiter.api.Test;
import svydovets.web.annotation.GetMapping;
import svydovets.web.dto.RequestInfoHolder;
import svydovets.web.util.RequestInfoHolderCreator;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestInfoHolderCreatorTest {

  @Test
  void shouldCreateRequestInfoHolder() {
    Method[] methods = SimpleRestController.class.getDeclaredMethods();
    assertEquals(1, methods.length);

    Method firstMethod = methods[0];
    assertTrue(firstMethod.isAnnotationPresent(GetMapping.class));

    RequestInfoHolder requestInfoHolder = RequestInfoHolderCreator
            .create(SimpleRestController.class.getSimpleName(), SimpleRestController.class, firstMethod);

    assertEquals(SimpleRestController.class.getSimpleName(), requestInfoHolder.getClassName());
    assertEquals(1, requestInfoHolder.getParameterNames().length);
    assertEquals(1, requestInfoHolder.getParameterTypes().length);
    assertEquals("hello", requestInfoHolder.getMethodName());
  }
}
