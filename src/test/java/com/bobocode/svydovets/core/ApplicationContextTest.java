package com.bobocode.svydovets.core;

import com.bobocode.svydovets.service.base.EditService;
import com.bobocode.svydovets.service.base.MessageService;
import com.bobocode.svydovets.service.ProductService;
import com.bobocode.svydovets.service.TrimService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import svydovets.core.context.ApplicationContext;
import svydovets.core.context.DefaultApplicationContext;
import svydovets.exception.NoSuchBeanException;
import svydovets.exception.NoUniqueBeanException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApplicationContextTest {

    @Test
    void createApplicationContextFromBasePackage() {
        ApplicationContext context = new DefaultApplicationContext("com.bobocode.svydovets.service.base");
        assertThat(context.getBean(MessageService.class)).isNotNull();
        assertThat(context.getBean(EditService.class)).isNotNull();
        // todo: It may be better to group the existing "services" in additional packages and check that all beans of
        //  specified "basePackage" are created
    }


    @Test
    void getBeanThrowNoSuchBeanException() {
        ApplicationContext context = new DefaultApplicationContext("com.bobocode.svydovets.service.base");
        assertThatExceptionOfType(NoSuchBeanException.class)
                .isThrownBy(() -> context.getBean(TrimService.class))
                .withMessage(String.format("No bean found of type %s", TrimService.class.getName()));
    }

    @Test
    void getBeanThrowNoUniqueBeanException() {
        ApplicationContext context = new DefaultApplicationContext("com.bobocode.svydovets.service");
        assertThatExceptionOfType(NoUniqueBeanException.class)
                .isThrownBy(() -> context.getBean(ProductService.class))
                .withMessage(String.format("No unique bean found of type %s", ProductService.class.getName()));
    }

    // todo: 1) Add tests for "getBean(String name, Class<T> requiredType)" method
    // todo: 2) Add tests for "getPreparedNoArgsConstructor()" method.
    //  2.1) Create a class with no default constructor.
    //  Problem is that other tests failed, because this class is scanned by "Reflections" even if it is in nested package

}
