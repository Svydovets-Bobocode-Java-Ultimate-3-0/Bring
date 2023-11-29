package com.bobocode.svydovets.ioc.core.beanFactory;

import com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface.InjCandidate;
import com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface.InjFirstCandidate;
import com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface.PrototypeCandidate;
import com.bobocode.svydovets.source.autowire.constructor.FirstInjectionCandidate;
import com.bobocode.svydovets.source.autowire.constructor.SecondInjectionCandidate;
import com.bobocode.svydovets.source.autowire.constructor.ValidConstructorInjectionService;
import com.bobocode.svydovets.source.autowire.method.ConfigMethodBasedBeanAutowiring;
import com.bobocode.svydovets.source.circularDependency.CircularDependencyConfig;
import com.bobocode.svydovets.source.circularDependency.FirstCircularDependencyOwner;
import com.bobocode.svydovets.source.circularDependency.SecondCircularDependencyOwner;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import svydovets.core.context.AnnotationConfigApplicationContext;
import svydovets.core.context.ApplicationContext;
import svydovets.exception.NoSuchBeanDefinitionException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import svydovets.core.context.beanFactory.BeanFactory;
import svydovets.exception.UnresolvedCircularDependencyException;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static svydovets.util.ErrorMessageConstants.CIRCULAR_DEPENDENCY_DETECTED;

class BeanFactoryTest {

    private BeanFactory beanFactory;

    @BeforeEach
    void setUp() {
        this.beanFactory = new BeanFactory();
    }


    @Test
    void shouldRegisterBeanWhenConfigClassIsPassed() {
        registerBeanDefinitionsForConfigMethodBaseBeanAutowiring();

        beanFactory.registerBeans(ConfigMethodBasedBeanAutowiring.class);
        ConfigMethodBasedBeanAutowiring config = beanFactory.getBean(ConfigMethodBasedBeanAutowiring.class);
        assertThat(config).isNotNull();
    }


    @Test
    void shouldRegisterMethodBasedBeanWhenConfigClassIsPassed() {
        registerBeanDefinitionsForConfigMethodBaseBeanAutowiring();

        beanFactory.registerBeans(ConfigMethodBasedBeanAutowiring.class);
        ValidConstructorInjectionService injectionService = beanFactory.getBean(ValidConstructorInjectionService.class);
        assertThat(injectionService).isNotNull();
        assertThat(injectionService.getFirstInjectionCandidate()).isNotNull();
        assertThat(injectionService.getSecondInjectionCandidate()).isNotNull();
    }

    @Test
    void shouldRegisterMethodArgumentBeansWhenConfigClassIsPassed() {
        registerBeanDefinitionsForConfigMethodBaseBeanAutowiring();

        beanFactory.registerBeans(ConfigMethodBasedBeanAutowiring.class);
        FirstInjectionCandidate firstInjectionCandidate = beanFactory.getBean(FirstInjectionCandidate.class);
        SecondInjectionCandidate secondInjectionCandidate = beanFactory.getBean(SecondInjectionCandidate.class);
        assertThat(firstInjectionCandidate).isNotNull();
        assertThat(secondInjectionCandidate).isNotNull();
    }

    @Test
    void shouldRegisterMethodArgumentBeansAndPassThemToMethodBasedBean() {
        registerBeanDefinitionsForConfigMethodBaseBeanAutowiring();

        beanFactory.registerBeans(ConfigMethodBasedBeanAutowiring.class);
        ValidConstructorInjectionService injectionService = beanFactory.getBean(ValidConstructorInjectionService.class);
        FirstInjectionCandidate firstInjectionCandidate = beanFactory.getBean(FirstInjectionCandidate.class);
        SecondInjectionCandidate secondInjectionCandidate = beanFactory.getBean(SecondInjectionCandidate.class);
        assertThat(injectionService.getFirstInjectionCandidate()).isEqualTo(firstInjectionCandidate);
        assertThat(injectionService.getSecondInjectionCandidate()).isEqualTo(secondInjectionCandidate);
    }

    @Test
    @Disabled
    void shouldThrowExceptionIfCircularDependencyDetectedInClassBasedBeans() {
        AssertionsForClassTypes.assertThatExceptionOfType(UnresolvedCircularDependencyException.class)
                .isThrownBy(() -> beanFactory.registerBeans(FirstCircularDependencyOwner.class, SecondCircularDependencyOwner.class))
                .withMessage(CIRCULAR_DEPENDENCY_DETECTED, SecondCircularDependencyOwner.class.getName());
    }

    @Test
    @Disabled
    void shouldThrowExceptionIfCircularDependencyDetectedInMethodBasedBeans() {
        AssertionsForClassTypes.assertThatExceptionOfType(UnresolvedCircularDependencyException.class)
                .isThrownBy(() -> beanFactory.registerBeans(CircularDependencyConfig.class))
                .withMessage(CIRCULAR_DEPENDENCY_DETECTED, SecondCircularDependencyOwner.class.getName());
    }

    private void registerBeanDefinitionsForConfigMethodBaseBeanAutowiring() {
        beanFactory.beanDefinitionFactory().registerBeanDefinitions(Set.of(
                        ConfigMethodBasedBeanAutowiring.class,
                        ValidConstructorInjectionService.class,
                        FirstInjectionCandidate.class,
                        SecondInjectionCandidate.class
                )
        );
    }
}