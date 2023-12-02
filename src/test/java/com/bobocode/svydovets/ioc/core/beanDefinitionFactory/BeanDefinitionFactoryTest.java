package com.bobocode.svydovets.ioc.core.beanDefinitionFactory;

import com.bobocode.svydovets.source.autowire.constructor.ValidConstructorInjectionService;
import com.bobocode.svydovets.source.autowire.constructor.InvalidConstructorInjectionService;
import com.bobocode.svydovets.source.autowire.constructor.FirstInjectionCandidate;
import com.bobocode.svydovets.source.autowire.method.CopyService;
import com.bobocode.svydovets.source.autowire.method.PrintLnService;
import com.bobocode.svydovets.source.autowire.method.TrimService;
import com.bobocode.svydovets.source.base.CommonService;
import com.bobocode.svydovets.source.base.MessageService;
import com.bobocode.svydovets.source.config.BasePackageBeansConfig;
import com.bobocode.svydovets.source.config.BasePackageWithAdditionalBeansConfig;
import com.bobocode.svydovets.source.config.ConfigWithThrowScope;
import org.junit.jupiter.api.*;
import svydovets.core.context.ApplicationContext;
import svydovets.core.context.beanDefinition.BeanAnnotationBeanDefinition;
import svydovets.core.context.beanDefinition.BeanDefinition;
import svydovets.core.context.beanDefinition.BeanDefinitionFactory;
import svydovets.core.context.beanDefinition.ComponentAnnotationBeanDefinition;
import svydovets.core.exception.BeanDefinitionCreateException;
import svydovets.core.exception.UnsupportedScopeException;
import svydovets.util.ErrorMessageConstants;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static svydovets.util.NameResolver.resolveBeanName;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BeanDefinitionFactoryTest {

    private BeanDefinitionFactory beanDefinitionFactory;

    @BeforeEach
    void setUp() {
        beanDefinitionFactory = new BeanDefinitionFactory();
    }

    @Test
    @Order(1)
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
    @Order(2)
    void shouldRegisterConfigClassesAsComponentAnnotationBeanDefinitions() {
        beanDefinitionFactory.registerBeanDefinitions(Set.of(BasePackageBeansConfig.class, BasePackageWithAdditionalBeansConfig.class));
        BeanDefinition basePackageBeanDefinition = beanDefinitionFactory.getBeanDefinitionByBeanName("basePackageBeansConfig");
        BeanDefinition baseAdditionalPackageBeanDefinition = beanDefinitionFactory.getBeanDefinitionByBeanName("basePackageWithAdditionalBeansConfig");
        assertThat(basePackageBeanDefinition).isNotNull();
        assertThat(baseAdditionalPackageBeanDefinition).isNotNull();
        assertThat(basePackageBeanDefinition.getClass()).isEqualTo(ComponentAnnotationBeanDefinition.class);
        assertThat(baseAdditionalPackageBeanDefinition.getClass()).isEqualTo(ComponentAnnotationBeanDefinition.class);
    }

    @Test
    @Order(3)
    void shouldRegisterFilledBeanAnnotationSingletonBeanDefinition() {
        beanDefinitionFactory.registerBeanDefinitions(Set.of(BasePackageWithAdditionalBeansConfig.class));
        BeanDefinition beanDefinition = beanDefinitionFactory.getBeanDefinitionByBeanName("copyService");

        assertThat(beanDefinition).isNotNull();
        assertThat(beanDefinition.getScope()).isEqualTo(ApplicationContext.SCOPE_SINGLETON);
        assertThat(beanDefinition.getBeanClass()).isEqualTo(CopyService.class);
    }

    @Test
    @Order(4)
    void shouldRegisterFilledBeanAnnotationPrototypeBeanDefinition() {
        beanDefinitionFactory.registerBeanDefinitions(Set.of(BasePackageWithAdditionalBeansConfig.class));
        BeanDefinition beanDefinition = beanDefinitionFactory.getBeanDefinitionByBeanName("printLnService");

        assertThat(beanDefinition).isNotNull();
        assertThat(beanDefinition.getScope()).isEqualTo(ApplicationContext.SCOPE_PROTOTYPE);
        assertThat(beanDefinition.getBeanClass()).isEqualTo(PrintLnService.class);
    }

    @Test
    @Order(5)
    void shouldThrowUnsupportedScopeExceptionWithNotValidScope() {
        String errorMessage = String.format(ErrorMessageConstants.UNSUPPORTED_SCOPE_TYPE, "hernya");
        assertThatExceptionOfType(UnsupportedScopeException.class)
                .isThrownBy(() -> beanDefinitionFactory.registerBeanDefinitions(Set.of(ConfigWithThrowScope.class)))
                .withMessage(errorMessage);
    }

    @Test
    @Order(6)
    void shouldRegisterComponentAnnotationBeanDefinition() {
        beanDefinitionFactory.registerBeanDefinition(CommonService.class);
        BeanDefinition commonServiceBeanDefinition = beanDefinitionFactory.getBeanDefinitionByBeanName("commonService");
        assertThat(commonServiceBeanDefinition).isNotNull();
        assertThat(commonServiceBeanDefinition.getClass()).isEqualTo(ComponentAnnotationBeanDefinition.class);
    }

    @Test
    @Order(7)
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
    @Order(7)
    void shouldReturnCorrectFilledBeanAnnotationBeanDefinition() throws Exception {
        Class<?> configClass = BasePackageWithAdditionalBeansConfig.class;
        Class<?> beanClass = TrimService.class;
        String beanName = "megaTrimService";

        beanDefinitionFactory.registerBeanDefinitions(Set.of(configClass));
        BeanAnnotationBeanDefinition serviceDefinition = (BeanAnnotationBeanDefinition) beanDefinitionFactory.getBeanDefinitionByBeanName(beanName);

        assertThat(serviceDefinition).isNotNull();
        assertThat(serviceDefinition.getBeanClass()).isEqualTo(beanClass);
        assertThat(serviceDefinition.getBeanName()).isEqualTo(beanName);
        assertThat(serviceDefinition.getConfigClassName()).isEqualTo(resolveBeanName(configClass));
        assertThat(serviceDefinition.getInitMethodOfBeanFromConfigClass()).isEqualTo(configClass.getDeclaredMethod("trimService"));
        assertThat(serviceDefinition.getScope()).isEqualTo(ApplicationContext.SCOPE_SINGLETON);
        assertThat(serviceDefinition.getCreationStatus()).isEqualTo(BeanDefinition.BeanCreationStatus.NOT_CREATED.name());
    }

    @Test
    @Order(8)
    void shouldThrowExceptionIfConstructorWithoutAutowire() {
        assertThrows(BeanDefinitionCreateException.class, () -> {
            beanDefinitionFactory.registerBeanDefinition(InvalidConstructorInjectionService.class);
        });
    }
}