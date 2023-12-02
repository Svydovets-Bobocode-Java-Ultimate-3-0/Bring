package com.bobocode.svydovets.web.util;

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
  void shouldCreateRequestInfoHolder() throws Exception {
    Class<SimpleRestController> simpleRestControllerClass = SimpleRestController.class;
    Method[] methods = simpleRestControllerClass.getDeclaredMethods();
    assertEquals(4, methods.length);

    Method firstMethod = simpleRestControllerClass.getDeclaredMethod("helloPath", String.class);
    assertTrue(firstMethod.isAnnotationPresent(GetMapping.class));

    RequestInfoHolder requestInfoHolder = RequestInfoHolderCreator
            .create(SimpleRestController.class.getSimpleName(), SimpleRestController.class, firstMethod);

    assertEquals(SimpleRestController.class.getSimpleName(), requestInfoHolder.getClassName());
    assertEquals(1, requestInfoHolder.getParameterNames().length);
    assertEquals(1, requestInfoHolder.getParameterTypes().length);
    assertEquals("helloPath", requestInfoHolder.getMethodName());
  }
}
