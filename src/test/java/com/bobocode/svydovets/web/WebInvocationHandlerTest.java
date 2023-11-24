package com.bobocode.svydovets.web;

import com.bobocode.svydovets.web.controller.UserController;
import com.bobocode.svydovets.web.dto.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import svydovets.web.WebInvocationHandler;
import svydovets.web.path.RequestInfo;

import java.lang.reflect.Method;
import java.util.Map;

import static com.bobocode.svydovets.web.factory.UserFactory.DEFAULT_FIRST_NAME;
import static com.bobocode.svydovets.web.factory.UserFactory.DEFAULT_ID;
import static com.bobocode.svydovets.web.factory.UserFactory.DEFAULT_LAST_NAME;
import static com.bobocode.svydovets.web.factory.UserFactory.DEFAULT_STATUS;
import static com.bobocode.svydovets.web.factory.UserFactory.createDefaultUser;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WebInvocationHandlerTest {

    private WebInvocationHandler webInvocationHandler;

    @BeforeEach
    public void setUp() {
        webInvocationHandler = new WebInvocationHandler();
    }

    @Test
    public void shouldExtractValueFromPathVariable() throws Exception {
        User expectedResult = createDefaultUser();
        String methodName = "getOneById";
        Method methodToInvoke = UserController.class.getDeclaredMethod(methodName, Long.class);
        RequestInfo requestInfo = buildRequestInfo(Map.of("id", DEFAULT_ID.toString()), emptyMap(), null);
        Object[] args = webInvocationHandler.invoke(methodToInvoke, requestInfo);

        User result = (User) methodToInvoke.invoke(new UserController(), args);
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    public void shouldExtractValueFromRequestParam() throws Exception {
        User expectedResult = createDefaultUser();
        String methodName = "getOneByFirstName";
        Method methodToInvoke = UserController.class.getDeclaredMethod(methodName, String.class);
        RequestInfo requestInfo = buildRequestInfo(emptyMap(), Map.of("firstName", new String[]{DEFAULT_FIRST_NAME}), null);
        Object[] args = webInvocationHandler.invoke(methodToInvoke, requestInfo);

        User result = (User) methodToInvoke.invoke(new UserController(), args);
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    public void shouldExtractValueFromRequestBody() throws Exception {
        User expectedResult = createDefaultUser();
        String userJson = new ObjectMapper().writeValueAsString(expectedResult);
        String methodName = "save";
        Method methodToInvoke = UserController.class.getDeclaredMethod(methodName, User.class);
        RequestInfo requestInfo = buildRequestInfo(emptyMap(), emptyMap(), userJson);
        Object[] args = webInvocationHandler.invoke(methodToInvoke, requestInfo);

        User result = (User) methodToInvoke.invoke(new UserController(), args);
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    public void shouldExtractAllRequestInfo() throws Exception {
        User expectedResult = new User(DEFAULT_ID + 1L, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_STATUS);

        String userJson = new ObjectMapper().writeValueAsString(createDefaultUser());
        String methodName = "update";
        Method methodToInvoke = UserController.class.getDeclaredMethod(methodName, Long.class, String.class, User.class);
        RequestInfo requestInfo = buildRequestInfo(Map.of("id", DEFAULT_ID.toString()), Map.of("status", new String[]{DEFAULT_STATUS}), userJson);
        Object[] args = webInvocationHandler.invoke(methodToInvoke, requestInfo);

        User result = (User) methodToInvoke.invoke(new UserController(), args);
        assertThat(result).isEqualTo(expectedResult);
    }

    private RequestInfo buildRequestInfo(Map<String, String> pathVariableValuesMap, Map<String, String[]> requestParameterValuesMap, String requestBody) {
        return new RequestInfo(
                pathVariableValuesMap,
                requestParameterValuesMap,
                requestBody
        );
    }
}
