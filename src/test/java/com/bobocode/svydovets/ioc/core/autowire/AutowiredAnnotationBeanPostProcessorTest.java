package com.bobocode.svydovets.ioc.core.autowire;

import com.bobocode.svydovets.source.autowire.field.EditService;
import com.bobocode.svydovets.source.autowire.method.OrderService;
import com.bobocode.svydovets.source.config.AutowirePackageBeansConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import svydovets.core.context.AnnotationConfigApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AutowiredAnnotationBeanPostProcessorTest {

    private AnnotationConfigApplicationContext context;

    @BeforeEach
    public void setUp() {
        context = new AnnotationConfigApplicationContext(AutowirePackageBeansConfig.class);
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

    @Test
    void shouldNotNullWhenFieldIsInjectedViaSetter() {
        OrderService orderService = context.getBean(OrderService.class);
        assertThat(orderService.getMessageService()).isNotNull();
        assertThat(orderService.getCommonService()).isNotNull();
    }


    @Test
    void shouldBeNullWhenFieldIsInjectedViaSetter() {
        OrderService orderService = context.getBean(OrderService.class);
        assertThat(orderService.getNullService()).isNull();
    }

}
