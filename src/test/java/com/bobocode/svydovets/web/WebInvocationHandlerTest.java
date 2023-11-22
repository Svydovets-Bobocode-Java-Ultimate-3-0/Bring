package com.bobocode.svydovets.web;

import com.bobocode.svydovets.web.controller.UserController;
import com.bobocode.svydovets.web.dto.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import svydovets.web.WebInvocationHandler;
import svydovets.web.dto.RequestInfoHolder;

import java.lang.reflect.Method;
import java.util.Map;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WebInvocationHandlerTest {

    private WebInvocationHandler webInvocationHandler;

    @BeforeEach
    public void setUp() {
        webInvocationHandler = new WebInvocationHandler();
    }

    @Test
    public void shouldExtractValueFromPathVariable() throws Exception {
        User expectedResult = new User(1L, "TestFirstName", "TestLastName");
        UserController userController = new UserController();
        Method getOneMethod = UserController.class.getDeclaredMethod("getOne", Long.class);

        // Pass parameters
        RequestInfoHolder requestInfoHolder = new RequestInfoHolder("userController");
        requestInfoHolder.setMethodName("getOne");
        requestInfoHolder.setParameterTypes(new Class[]{Long.class});
        Object[] args = webInvocationHandler.invoke(UserController.class, Map.of("arg0", 1L), requestInfoHolder);

        User result = (User) getOneMethod.invoke(userController, args);
        Assertions.assertThat(result).isEqualTo(expectedResult);
    }
}
