package com.bobocode.svydovets.web.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import svydovets.web.dto.RequestInfoHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RequestInfoHolderTest {

    private RequestInfoHolder requestInfoHolder;

    @BeforeEach
    void init() {
        this.requestInfoHolder = new RequestInfoHolder();
    }

    @Test
    public void shouldReturnClassTypeField() {
        requestInfoHolder.setClassType(User.class);
        assertEquals(User.class, requestInfoHolder.getClassType());
    }

    @Test
    public void shouldReturnClassNameField() {
        requestInfoHolder.setClassName("user");
        assertEquals("user", requestInfoHolder.getClassName());
    }

    @Test
    public void shouldReturnMethodNameField() {
        requestInfoHolder.setMethodName("userMethod");
        assertEquals("userMethod", requestInfoHolder.getMethodName());
    }

    @Test
    public void shouldReturnParameterNamesField() {
        String[] parameters = new String[]{"parameter1", "parameter2"};
        requestInfoHolder.setParameterNames(parameters);
        assertEquals(2, requestInfoHolder.getParameterNames().length);
        assertEquals(parameters[0], requestInfoHolder.getParameterNames()[0]);
        assertEquals(parameters[1], requestInfoHolder.getParameterNames()[1]);
    }

    @Test
    public void shouldReturnParameterTypesField() {
        Class<?>[] parameters = new Class<?>[]{User.class};
        requestInfoHolder.setParameterTypes(parameters);
        assertEquals(1, requestInfoHolder.getParameterTypes().length);
        assertEquals(parameters[0], requestInfoHolder.getParameterTypes()[0]);
    }
}
