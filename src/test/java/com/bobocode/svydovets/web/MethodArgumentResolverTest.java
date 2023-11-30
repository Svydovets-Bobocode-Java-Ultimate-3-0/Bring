package com.bobocode.svydovets.web;

import com.bobocode.svydovets.web.controller.UserController;
import com.bobocode.svydovets.web.dto.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import svydovets.web.DispatcherServlet;
import svydovets.web.MethodArgumentResolver;
import svydovets.web.ServletWebRequest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

import static com.bobocode.svydovets.web.factory.UserFactory.DEFAULT_FIRST_NAME;
import static com.bobocode.svydovets.web.factory.UserFactory.DEFAULT_ID;
import static com.bobocode.svydovets.web.factory.UserFactory.DEFAULT_LAST_NAME;
import static com.bobocode.svydovets.web.factory.UserFactory.createDefaultUser;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MethodArgumentResolverTest {

    private MethodArgumentResolver methodArgumentResolver;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    public void setUp() {
        methodArgumentResolver = new MethodArgumentResolver();
    }

    @Test
    public void shouldExtractValueFromPathVariable() throws Exception {
        Object[] expectedArgs = new Object[]{DEFAULT_ID};
        String methodName = "getOneById";
        Method methodToInvoke = UserController.class.getDeclaredMethod(methodName, Long.class);
        when(request.getServletPath()).thenReturn("/users/1");
        when(request.getAttribute(DispatcherServlet.CONTROLLER_REDIRECT_REQUEST_PATH)).thenReturn("/users/{id}");

        ServletWebRequest servletWebRequest = new ServletWebRequest(request, response);
        Object[] actualArgs = methodArgumentResolver.resolveArguments(methodToInvoke, servletWebRequest);

        assertThat(actualArgs.length).isEqualTo(expectedArgs.length);
        assertThat(actualArgs[0]).isEqualTo(expectedArgs[0]);
    }

    @Test
    public void shouldExtractValueFromRequestParam() throws Exception {
        Object[] expectedArgs = new Object[]{DEFAULT_FIRST_NAME};
        String methodName = "getOneByFirstName";
        Method methodToInvoke = UserController.class.getDeclaredMethod(methodName, String.class);
        when(request.getParameter("firstName")).thenReturn(DEFAULT_FIRST_NAME);

        ServletWebRequest servletWebRequest = new ServletWebRequest(request, response);
        Object[] actualArgs = methodArgumentResolver.resolveArguments(methodToInvoke, servletWebRequest);

        assertThat(actualArgs.length).isEqualTo(expectedArgs.length);
        assertThat(actualArgs[0]).isEqualTo(expectedArgs[0]);
    }

    @Test
    public void shouldExtractValueFromRequestBody() throws Exception {
        User defaultUser = createDefaultUser();
        Object[] expectedArgs = new Object[]{defaultUser};
        String userJson = new ObjectMapper().writeValueAsString(defaultUser);
        String methodName = "save";
        Method methodToInvoke = UserController.class.getDeclaredMethod(methodName, User.class);

        when(request.getReader()).thenReturn(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(userJson.getBytes()))));

        ServletWebRequest servletWebRequest = new ServletWebRequest(request, response);
        Object[] actualArgs = methodArgumentResolver.resolveArguments(methodToInvoke, servletWebRequest);

        assertThat(actualArgs.length).isEqualTo(expectedArgs.length);
        assertThat(actualArgs[0]).isEqualTo(expectedArgs[0]);
    }

    @Test
    public void shouldExtractAllRequestInfo() throws Exception {
        Long id = 100L;
        String status = "UPDATED";
        User user = new User(id, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, status);

        Object[] expectedArgs = new Object[]{id, status, user};

        String userJson = new ObjectMapper().writeValueAsString(user);
        String methodName = "update";
        Method methodToInvoke = UserController.class.getDeclaredMethod(methodName, Long.class, String.class, User.class);

        when(request.getServletPath()).thenReturn(String.format("/users/%d?status=%s", id, status));
        when(request.getAttribute(DispatcherServlet.CONTROLLER_REDIRECT_REQUEST_PATH)).thenReturn("/users/{id}");
        when(request.getParameter("status")).thenReturn(status);
        when(request.getReader()).thenReturn(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(userJson.getBytes()))));


        ServletWebRequest servletWebRequest = new ServletWebRequest(request, response);
        Object[] actualArgs = methodArgumentResolver.resolveArguments(methodToInvoke, servletWebRequest);

        assertThat(actualArgs.length).isEqualTo(expectedArgs.length);
        assertThat(actualArgs[0]).isEqualTo(expectedArgs[0]);
        assertThat(actualArgs[1]).isEqualTo(expectedArgs[1]);
        assertThat(actualArgs[2]).isEqualTo(expectedArgs[2]);
    }

    @Test
    public void shouldExtractHttpServletRequestAndHttpServletResponse() throws Exception {
        Object[] expectedArgs = new Object[]{request, response};
        Method methodToInvoke = UserController.class.getDeclaredMethod("removeWithServletRequestAndResponse", HttpServletRequest.class, HttpServletResponse.class);

        ServletWebRequest servletWebRequest = new ServletWebRequest(request, response);
        Object[] actualArgs = methodArgumentResolver.resolveArguments(methodToInvoke, servletWebRequest);
        methodToInvoke.invoke(new UserController(), actualArgs);

        assertThat(actualArgs.length).isEqualTo(expectedArgs.length);
        assertThat(actualArgs[0]).isEqualTo(expectedArgs[0]);
        assertThat(actualArgs[1]).isEqualTo(expectedArgs[1]);
    }
}
