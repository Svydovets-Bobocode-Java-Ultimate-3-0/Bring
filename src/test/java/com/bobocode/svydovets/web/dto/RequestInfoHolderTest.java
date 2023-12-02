package com.bobocode.svydovets.web.dto;

import com.bobocode.svydovets.web.dto.User;
import org.junit.jupiter.api.*;
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
    @Order(1)
    public void shouldReturnClassTypeField() {
        requestInfoHolder.setClassType(User.class);
        assertEquals(User.class, requestInfoHolder.getClassType());
    }

    @Test
    @Order(2)
    public void shouldReturnClassNameField() {
        requestInfoHolder.setClassName("user");
        assertEquals("user", requestInfoHolder.getClassName());
    }

    @Test
    @Order(3)
    public void shouldReturnMethodNameField() {
        requestInfoHolder.setMethodName("userMethod");
        assertEquals("userMethod", requestInfoHolder.getMethodName());
    }

    @Test
    @Order(4)
    public void shouldReturnParameterNamesField() {
        String[] parameters = new String[]{"parameter1", "parameter2"};
        requestInfoHolder.setParameterNames(parameters);
        assertEquals(2, requestInfoHolder.getParameterNames().length);
        assertEquals(parameters[0], requestInfoHolder.getParameterNames()[0]);
        assertEquals(parameters[1], requestInfoHolder.getParameterNames()[1]);
    }

    @Test
    @Order(5)
    public void shouldReturnParameterTypesField() {
        Class<?>[] parameters = new Class<?>[]{User.class};
        requestInfoHolder.setParameterTypes(parameters);
        assertEquals(1, requestInfoHolder.getParameterTypes().length);
        assertEquals(parameters[0], requestInfoHolder.getParameterTypes()[0]);
    }
}
