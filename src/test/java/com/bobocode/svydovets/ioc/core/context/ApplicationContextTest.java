package com.bobocode.svydovets.ioc.core.context;

import com.bobocode.svydovets.source.autowire.method.TrimService;
import com.bobocode.svydovets.source.base.CommonService;
import com.bobocode.svydovets.source.base.MessageService;
import com.bobocode.svydovets.source.base.NullService;
import com.bobocode.svydovets.source.config.BasePackageBeansConfig;
import com.bobocode.svydovets.source.config.QualifierPackageBeansConfig;
import com.bobocode.svydovets.source.qualifier.withoutPrimary.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import svydovets.core.context.AnnotationConfigApplicationContext;
import svydovets.core.context.ApplicationContext;
import svydovets.exception.NoSuchBeanException;
import svydovets.exception.NoUniqueBeanException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApplicationContextTest {

    @BeforeEach
    void setUp() {

    }

    @Test
    @Order(1)
    void shouldCreateApplicationContextFromBasePackage() {
        ApplicationContext context = new AnnotationConfigApplicationContext("com.bobocode.svydovets.service.base");
        assertThat(context).isNotNull();
    }

    @Test
    @Order(2)
    void shouldCreateApplicationContextFromConfigClass() {
        ApplicationContext context = new AnnotationConfigApplicationContext(BasePackageBeansConfig.class);
        assertThat(context).isNotNull();
    }

    @Test
    @Order(3)
    void shouldCreateAllRequiredBeansFromBasePackage() {
        String basePackage = "com.bobocode.svydovets.source.base";

        ApplicationContext context = new AnnotationConfigApplicationContext(basePackage);

        assertThat(context.getBean(CommonService.class)).isNotNull();
        assertThat(context.getBean(MessageService.class)).isNotNull();
        assertThat(context.getBean(NullService.class)).isNotNull();
    }

    @Test
    @Order(4)
    void shouldCreatesAllRequiredBeansFromConfigClass() {
        ApplicationContext context = new AnnotationConfigApplicationContext(BasePackageBeansConfig.class);

        assertThat(context.getBean(BasePackageBeansConfig.class)).isNotNull();
        assertThat(context.getBean(CommonService.class)).isNotNull();
        assertThat(context.getBean(MessageService.class)).isNotNull();
        assertThat(context.getBean(NullService.class)).isNotNull();
    }


    @Test
    @Order(5)
    void shouldThrowNoSuchBeanExceptionWhenBeanIsNotPresent() {
        ApplicationContext context = new AnnotationConfigApplicationContext(BasePackageBeansConfig.class);
        assertThatExceptionOfType(NoSuchBeanException.class)
                .isThrownBy(() -> context.getBean(TrimService.class))
                .withMessage(String.format("No bean found of type %s", TrimService.class.getName()));
    }

    @Test
    @Order(6)
    void shouldThrowNoUniqueBeanExceptionWhenNoUniqueBeanIsPresent() {
        ApplicationContext context = new AnnotationConfigApplicationContext(QualifierPackageBeansConfig.class);
        assertThatExceptionOfType(NoUniqueBeanException.class)
                .isThrownBy(() -> context.getBean(PaymentService.class))
                .withMessage(String.format("No unique bean found of type %s", PaymentService.class.getName()));
    }

    @Test
    @Order(7)
    void shouldThrowNoSuchBeanExceptionWhenBeanIsNotPresentByName() {
        ApplicationContext context = new AnnotationConfigApplicationContext(BasePackageBeansConfig.class);
        assertThatExceptionOfType(NoSuchBeanException.class)
                .isThrownBy(() -> context.getBean("superMessageService", MessageService.class))
                .withMessage(String.format("No bean found of type %s", MessageService.class.getName()));
    }

    @Test
    @Order(8)
    void shouldThrowNoSuchBeanExceptionWhenBeanIsPresentByNameButDifferentClassType() {
        ApplicationContext context = new AnnotationConfigApplicationContext(BasePackageBeansConfig.class);
        assertThatExceptionOfType(ClassCastException.class)
                .isThrownBy(() -> context.getBean("messageService", CommonService.class))
                .withMessage(String.format(
                        "Cannot cast %s to %s",
                        MessageService.class.getName(),
                        CommonService.class.getName())
                );
    }

    @Test
    @Order(9)
    void shouldReturnBeanByNameAndClassType() {
        ApplicationContext context = new AnnotationConfigApplicationContext(BasePackageBeansConfig.class);
        MessageService messageService = context.getBean("messageService", MessageService.class);
        assertThat(messageService).isNotNull();
    }
    // todo: 1) Add tests for "getBean(String name, Class<T> requiredType)" method
    // todo: 2) Add tests for "getBeansOfType(Class<T> requiredType)" method
    // todo: 3) Add tests for "getPreparedNoArgsConstructor()" method.
    //  3.1) Create a class with no default constructor.
    //  Problem is that other tests failed, because this class is scanned by "Reflections" even if it is in nested package

}
