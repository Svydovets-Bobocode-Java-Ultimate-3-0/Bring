package com.bobocode.svydovets.ioc.core.beanDefinitionFactory;

import com.bobocode.svydovets.source.autowire.constructor.ValidConstructorInjectionService;
import com.bobocode.svydovets.source.autowire.constructor.InvalidConstructorInjectionService;
import com.bobocode.svydovets.source.autowire.constructor.FirstInjectionCandidate;
import com.bobocode.svydovets.source.autowire.method.TrimService;
import com.bobocode.svydovets.source.base.CommonService;
import com.bobocode.svydovets.source.base.MessageService;
import com.bobocode.svydovets.source.config.BasePackageBeansConfig;
import com.bobocode.svydovets.source.config.BasePackageWithAdditionalBeansConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import svydovets.core.context.ApplicationContext;
import svydovets.core.context.beanDefinition.BeanAnnotationBeanDefinition;
import svydovets.core.context.beanDefinition.BeanDefinition;
import svydovets.core.context.beanDefinition.BeanDefinitionFactory;
import svydovets.core.context.beanDefinition.ComponentAnnotationBeanDefinition;
import svydovets.exception.BeanDefinitionCreateException;
import svydovets.util.BeanNameResolver;

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
        BeanDefinition commonServiceBeanDefinition = beanDefinitionFactory.getBeanDefinitionByBeanName("commonService");
        BeanDefinition messageServiceBeanDefinition = beanDefinitionFactory.getBeanDefinitionByBeanName("messageService");
        assertThat(commonServiceBeanDefinition).isNotNull();
        assertThat(messageServiceBeanDefinition).isNotNull();
        assertThat(commonServiceBeanDefinition.getClass()).isEqualTo(ComponentAnnotationBeanDefinition.class);
        assertThat(messageServiceBeanDefinition.getClass()).isEqualTo(ComponentAnnotationBeanDefinition.class);
    }

    @Test
    void shouldRegisterConfigClassesAsComponentAnnotationBeanDefinitions() {
        beanDefinitionFactory.registerBeanDefinitions(Set.of(BasePackageBeansConfig.class, BasePackageWithAdditionalBeansConfig.class));
        BeanDefinition basePackageBeanDefinition = beanDefinitionFactory.getBeanDefinitionByBeanName("basePackageBeansConfig");
        BeanDefinition baseAdditionalPackageBeanDefinition = beanDefinitionFactory.getBeanDefinitionByBeanName("basePackageWithAdditionalBeansConfig");
        assertThat(basePackageBeanDefinition).isNotNull();
        assertThat(baseAdditionalPackageBeanDefinition).isNotNull();
        assertThat(basePackageBeanDefinition.getClass()).isEqualTo(ComponentAnnotationBeanDefinition.class);
        assertThat(baseAdditionalPackageBeanDefinition.getClass()).isEqualTo(ComponentAnnotationBeanDefinition.class);
//        BeanDefinition trimServiceBeanDefinition = beanDefinitionFactory.getBeanDefinitionByBeanName("megaTrimService");
//        BeanDefinition orderBeanDefinition = beanDefinitionFactory.getBeanDefinitionByBeanName("orderService");
//        assertThat(trimServiceBeanDefinition).isNotNull();
//        assertThat(orderBeanDefinition).isNotNull();
//        String scope = trimServiceBeanDefinition.getScope();
//        assertThat(trimServiceBeanDefinition.getClass()).isEqualTo(BeanAnnotationBeanDefinition.class);
//        assertThat(scope).isEqualTo(ApplicationContext.SCOPE_SINGLETON);
    }

    @Test
    void shouldRegisterComponentAnnotationBeanDefinition() {
        beanDefinitionFactory.registerBeanDefinition(CommonService.class);
        BeanDefinition commonServiceBeanDefinition = beanDefinitionFactory.getBeanDefinitionByBeanName("commonService");
        assertThat(commonServiceBeanDefinition).isNotNull();
        assertThat(commonServiceBeanDefinition.getClass()).isEqualTo(ComponentAnnotationBeanDefinition.class);
    }

    // Test to check that for methods marked @Bean creates BeanAnnotationBeanDefinition
    // Перевірити, що відпрацьовують помилки "UnsupportedScopeException"

    @Test
    void shouldReturnCorrectFilledComponentAnnotationBeanDefinition() {
        beanDefinitionFactory.registerBeanDefinition(ValidConstructorInjectionService.class);
        ComponentAnnotationBeanDefinition serviceDefinition = (ComponentAnnotationBeanDefinition) beanDefinitionFactory.getBeanDefinitionByBeanName("validConstructorInjectionService");
        assertThat(serviceDefinition).isNotNull();
        assertThat(serviceDefinition.getInitializationConstructor()).isNotNull();
        assertThat(serviceDefinition.getInitializationConstructor().getParameterTypes()[0]).isEqualTo(FirstInjectionCandidate.class);
        assertThat(serviceDefinition.getScope()).isEqualTo(ApplicationContext.SCOPE_SINGLETON);
        assertThat(serviceDefinition.getCreationStatus()).isEqualTo(BeanDefinition.BeanCreationStatus.NOT_CREATED.name());
    }

    @Test
    void shouldReturnCorrectFilledBeanAnnotationBeanDefinition() throws Exception {
        Class<?> configClass = BasePackageWithAdditionalBeansConfig.class;
        Class<?> beanClass = TrimService.class;
        String beanName = "megaTrimService";

        beanDefinitionFactory.registerBeanDefinitions(Set.of(configClass));
        BeanAnnotationBeanDefinition serviceDefinition = (BeanAnnotationBeanDefinition) beanDefinitionFactory.getBeanDefinitionByBeanName(beanName);

        assertThat(serviceDefinition).isNotNull();
        assertThat(serviceDefinition.getBeanClass()).isEqualTo(beanClass);
        assertThat(serviceDefinition.getBeanName()).isEqualTo(beanName);
        assertThat(serviceDefinition.getConfigClassName()).isEqualTo(BeanNameResolver.resolveBeanName(configClass));
        assertThat(serviceDefinition.getInitMethodOfBeanFromConfigClass()).isEqualTo(configClass.getDeclaredMethod("trimService"));
        assertThat(serviceDefinition.getScope()).isEqualTo(ApplicationContext.SCOPE_SINGLETON);
        assertThat(serviceDefinition.getCreationStatus()).isEqualTo(BeanDefinition.BeanCreationStatus.NOT_CREATED.name());
    }


    @Test
    void shouldThrowExceptionIfConstructorWithoutAutowire() {
        assertThrows(BeanDefinitionCreateException.class, () -> {
            beanDefinitionFactory.registerBeanDefinition(InvalidConstructorInjectionService.class);
        });
    }
}