package com.bobocode.svydovets.core;

import com.bobocode.svydovets.service.base.EditService;
import com.bobocode.svydovets.service.base.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import svydovets.core.context.AnnotationConfigApplicationContext;
import svydovets.core.context.ApplicationContext;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AutowireTest {

    private final static String BASE_PACKAGE = "com.bobocode.svydovets.service.base";
    private ApplicationContext context;

    @BeforeEach
    public void setUp(){
        context = new AnnotationConfigApplicationContext(BASE_PACKAGE);
    }

    @Test
    void shouldNotNullWhenFieldIsInjected(){
        EditService editService = context.getBean(EditService.class);
        assertThat(editService.getMessageService()).isNotNull();
    }

    @Test
    void shouldReturnNullIfFieldNotInjected(){
        EditService editService = context.getBean(EditService.class);
        assertThat(editService.getCommonService()).isNull();
    }

    @Test
    void shouldNotNullWhenFieldIsInjectedViaSetter(){
        OrderService orderService = context.getBean(OrderService.class);
        assertThat(orderService.getMessageService()).isNotNull();
        assertThat(orderService.getCommonService()).isNotNull();
    }


    @Test
    void shouldBeNullWhenFieldIsInjectedViaSetter(){
        OrderService orderService = context.getBean(OrderService.class);
        assertThat(orderService.getNullService()).isNull();
    }

}
