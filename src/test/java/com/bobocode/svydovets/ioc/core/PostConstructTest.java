package com.bobocode.svydovets.ioc.core;

import com.bobocode.svydovets.source.postconstruct.valid.PostConstructService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import svydovets.core.context.AnnotationConfigApplicationContext;
import svydovets.core.context.ApplicationContext;
import svydovets.core.exception.NoUniquePostConstructException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PostConstructTest {

    @Test
    @Order(1)
    void shouldInvokeThePostConstructMethodAfterBeanInitialization() {
        ApplicationContext context = new AnnotationConfigApplicationContext("com.bobocode.svydovets.source.postconstruct.valid");
        PostConstructService postConstructService = context.getBean(PostConstructService.class);
        Assertions.assertThat(postConstructService.getName()).isEqualTo("post-construct-service");
    }

    @Test
    @Order(2)
    void shouldThrowExceptionWhenServiceHasTwoPostConstructAnnotations() {
        assertThatExceptionOfType(NoUniquePostConstructException.class)
                .isThrownBy(() -> new AnnotationConfigApplicationContext("com.bobocode.svydovets.source.postconstruct.invalid"))
                .withMessage("You cannot have more than one method that is annotated with @PostConstruct.");
    }

}
