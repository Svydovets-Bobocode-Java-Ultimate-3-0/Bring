package com.bobocode.svydovets.ioc.core.autowire;

import com.bobocode.svydovets.source.autowire.field.EditService;
import com.bobocode.svydovets.source.autowire.method.OrderService;
import com.bobocode.svydovets.source.base.MessageService;
import com.bobocode.svydovets.source.config.MockConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import svydovets.core.context.AnnotationConfigApplicationContext;
import svydovets.core.context.ApplicationContext;
import svydovets.util.PackageScanner;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AutowiredAnnotationBeanPostProcessorTest {

    @InjectMocks
    private AnnotationConfigApplicationContext context;
    @Mock
    private PackageScanner packageScanner;

    @Test
    void shouldNotNullWhenFieldIsInjected() {
        when(packageScanner.findAllBeanCandidatesByBaseClass(Set.class))
                .thenReturn(Set.of(EditService.class, MessageService.class));

        EditService editService = context.getBean(EditService.class);
        assertThat(editService.getMessageService()).isNotNull();
    }

    @Test
    void shouldReturnNullIfFieldNotInjected() {
        ApplicationContext context = new AnnotationConfigApplicationContext(MockConfig.class);

        EditService editService = context.getBean(EditService.class);
        assertThat(editService.getCommonService()).isNull();
    }

    @Test
    void shouldNotNullWhenFieldIsInjectedViaSetter() {
        ApplicationContext context = new AnnotationConfigApplicationContext(MockConfig.class);

        OrderService orderService = context.getBean(OrderService.class);
        assertThat(orderService.getMessageService()).isNotNull();
        assertThat(orderService.getCommonService()).isNotNull();
    }


    @Test
    void shouldBeNullWhenFieldIsInjectedViaSetter() {
        ApplicationContext context = new AnnotationConfigApplicationContext(MockConfig.class);

        OrderService orderService = context.getBean(OrderService.class);
        assertThat(orderService.getNullService()).isNull();
    }

}
