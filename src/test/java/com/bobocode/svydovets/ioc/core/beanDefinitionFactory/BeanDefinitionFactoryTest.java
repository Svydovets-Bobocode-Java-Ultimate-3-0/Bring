package com.bobocode.svydovets.ioc.core.beanDefinitionFactory;

import com.bobocode.svydovets.source.autowire.constructor.ConstructorInjectionService;
import com.bobocode.svydovets.source.autowire.constructor.ConstructorInjectionServiceWithoutAutowire;
import com.bobocode.svydovets.source.autowire.constructor.InjectionCandidate;
import com.bobocode.svydovets.source.base.CommonService;
import com.bobocode.svydovets.source.base.MessageService;
import com.bobocode.svydovets.source.config.BasePackageWithAdditionalBeansConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import svydovets.core.context.beanDefinition.BeanAnnotationBeanDefinition;
import svydovets.core.context.beanDefinition.BeanDefinition;
import svydovets.core.context.beanDefinition.BeanDefinitionFactory;
import svydovets.core.context.beanDefinition.ComponentAnnotationBeanDefinition;
import svydovets.exception.BeanDefinitionCreateException;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BeanDefinitionFactoryTest {

    private BeanDefinitionFactory beanDefinitionFactory;

    @BeforeEach
    void setUp() {
        beanDefinitionFactory = new BeanDefinitionFactory();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void shouldRegisterComponentAnnotationBeanDefinitions() {
        beanDefinitionFactory.registerBeanDefinitions(Set.of(CommonService.class, MessageService.class));
        BeanDefinition commonServiceBeandefinition = beanDefinitionFactory.getBeanDefinitionByBeanName("commonService");
        BeanDefinition messageServiceBeanDefinition = beanDefinitionFactory.getBeanDefinitionByBeanName("messageService");
        assertThat(commonServiceBeandefinition).isNotNull();
        assertThat(messageServiceBeanDefinition).isNotNull();
        assertThat(commonServiceBeandefinition.getClass()).isEqualTo(ComponentAnnotationBeanDefinition.class);
        assertThat(messageServiceBeanDefinition.getClass()).isEqualTo(ComponentAnnotationBeanDefinition.class);
    }

    @Test
    void shouldRegisterComponentAnnotationBeanDefinition() {
        beanDefinitionFactory.registerBeanDefinition(CommonService.class);
        BeanDefinition commonServiceBeanDefinition = beanDefinitionFactory.getBeanDefinitionByBeanName("commonService");
        assertThat(commonServiceBeanDefinition).isNotNull();
        assertThat(commonServiceBeanDefinition.getClass()).isEqualTo(ComponentAnnotationBeanDefinition.class);
    }

    @Test
    void shouldReturnBeanFromConfigClassWith() {
        beanDefinitionFactory.registerBeanDefinitions(Set.of(CommonService.class, BasePackageWithAdditionalBeansConfig.class));
        BeanDefinition trimServiceBeanDefinition = beanDefinitionFactory.getBeanDefinitionByBeanName("megaTrimService");
        BeanDefinition orderBeanDefinition = beanDefinitionFactory.getBeanDefinitionByBeanName("orderService");
        assertThat(trimServiceBeanDefinition).isNotNull();
        assertThat(orderBeanDefinition).isNotNull();
        String scope = trimServiceBeanDefinition.getScope();
        assertThat(trimServiceBeanDefinition.getClass()).isEqualTo(BeanAnnotationBeanDefinition.class);
        assertThat(scope).isEqualTo("singleton");
    }

    @Test
    void shouldReturnCollectDefinitionFromServiceWithConstructorInjection() {
        beanDefinitionFactory.registerBeanDefinition(ConstructorInjectionService.class);
        ComponentAnnotationBeanDefinition serviceDefinition = (ComponentAnnotationBeanDefinition) beanDefinitionFactory.getBeanDefinitionByBeanName("constructorInjectionService");
        assertThat(serviceDefinition.getClass()).isEqualTo(ComponentAnnotationBeanDefinition.class);
        assertThat(serviceDefinition).isNotNull();
        assertThat(serviceDefinition.getScope()).isEqualTo("singleton");
        assertThat(serviceDefinition.getInitializationConstructor()).isNotNull();
        assertThat(serviceDefinition.getInitializationConstructor().getParameterTypes()[0]).isEqualTo(InjectionCandidate.class);
    }


    @Test
    void shouldThrowExceptionIfConstructorWithoutAutowire() {
        assertThrows(BeanDefinitionCreateException.class, () -> {
            beanDefinitionFactory.registerBeanDefinition(ConstructorInjectionServiceWithoutAutowire.class);
        });
    }
}