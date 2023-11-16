package com.bobocode.svydovets.core;

import com.bobocode.svydovets.service.base.EditService;
import com.bobocode.svydovets.service.base.OrderService;
import org.junit.jupiter.api.Test;
import svydovets.core.context.AnnotationConfigApplicationContext;
import svydovets.core.context.ApplicationContext;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AutowireTest {

    @Test
    void shouldNotNullWhenFieldIsInjected(){
        ApplicationContext context = new AnnotationConfigApplicationContext("com.bobocode.svydovets.service.base");
        EditService editService = context.getBean(EditService.class);
        assertThat(editService.getMessageService()).isNotNull();
    }

    @Test
    void shouldReturnNullIfFieldNotInjected(){
        ApplicationContext context = new AnnotationConfigApplicationContext("com.bobocode.svydovets.service.base");
        EditService editService = context.getBean(EditService.class);
        assertThat(editService.getCommonService()).isNull();
    }

    @Test
    void shouldNotNullWhenFieldIsInjectedViaSetter(){
        ApplicationContext context = new AnnotationConfigApplicationContext("com.bobocode.svydovets.service.base");
        OrderService orderService = context.getBean(OrderService.class);
        assertThat(orderService.getMessageService()).isNotNull();
        assertThat(orderService.getCommonService()).isNotNull();
    }


    @Test
    void shouldBeNullWhenFieldIsInjectedViaSetter(){
        ApplicationContext context = new AnnotationConfigApplicationContext("com.bobocode.svydovets.service.base");
        OrderService orderService = context.getBean(OrderService.class);
        assertThat(orderService.getNullService()).isNull();
    }

}
