package com.bobocode.svydovets.ioc.core.autowire;

import com.bobocode.svydovets.source.autowire.field.EditService;
import com.bobocode.svydovets.source.config.AutowireByFieldPackageBeansConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import svydovets.core.context.AnnotationConfigApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AutowiredFieldAnnotationBeanPostProcessorTest {

    private AnnotationConfigApplicationContext context;

    @BeforeEach
    public void setUp() {
        context = new AnnotationConfigApplicationContext(AutowireByFieldPackageBeansConfig.class);
    }

    @Test
    void shouldNotNullWhenFieldIsInjected() {
        EditService editService = context.getBean(EditService.class);
        assertThat(editService.getMessageService()).isNotNull();
    }

    @Test
    void shouldReturnNullIfFieldNotInjected() {
        EditService editService = context.getBean(EditService.class);
        assertThat(editService.getCommonService()).isNull();
    }
}
