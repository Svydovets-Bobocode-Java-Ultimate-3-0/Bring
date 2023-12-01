package com.bobocode.svydovets.ioc.core.autowire;

import com.bobocode.svydovets.source.autowire.method.OrderService;
import com.bobocode.svydovets.source.config.AutowireByMethodPackageBeansConfig;
import org.junit.jupiter.api.*;
import svydovets.core.context.AnnotationConfigApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AutowiredMethodAnnotationBeanPostProcessorTest {

    private AnnotationConfigApplicationContext context;

    @BeforeEach
    public void setUp() {
        context = new AnnotationConfigApplicationContext(AutowireByMethodPackageBeansConfig.class);
    }

    @Test
    @Order(1)
    void shouldNotNullWhenFieldIsInjectedViaSetter() {
        OrderService orderService = context.getBean(OrderService.class);
        assertThat(orderService.getMessageService()).isNotNull();
        assertThat(orderService.getCommonService()).isNotNull();
    }


    @Test
    @Order(2)
    void shouldBeNullWhenFieldIsInjectedViaSetter() {
        OrderService orderService = context.getBean(OrderService.class);
        assertThat(orderService.getNullService()).isNull();
    }

}
