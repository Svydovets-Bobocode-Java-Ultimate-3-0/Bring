package com.bobocode.svydovets.ioc.core;

import com.bobocode.svydovets.source.postconstruct.valid.PostConstructService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import svydovets.core.context.AnnotationConfigApplicationContext;
import svydovets.core.context.ApplicationContext;
import svydovets.exception.NoUniquePostConstructException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class PostConstructTest {

    @Test
    void shouldInvokeThePostConstructMethodAfterBeanInitialization() {
        ApplicationContext context = new AnnotationConfigApplicationContext("com.bobocode.svydovets.source.postconstruct.valid");
        PostConstructService postConstructService = context.getBean(PostConstructService.class);
        Assertions.assertThat(postConstructService.getName()).isEqualTo("post-construct-service");
    }

    @Test
    void shouldThrowExceptionWhenServiceHasTwoPostConstructAnnotations() {
        assertThatExceptionOfType(NoUniquePostConstructException.class)
                .isThrownBy(() -> new AnnotationConfigApplicationContext("com.bobocode.svydovets.source.postconstruct.invalid"))
                .withMessage("You cannot have more than one method that is annotated with @PostConstruct.");
    }

}