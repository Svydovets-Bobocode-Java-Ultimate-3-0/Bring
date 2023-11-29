package com.bobocode.svydovets.ioc.core.beanFactory;

import com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface.InjCandidate;
import com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface.InjFirstCandidate;
import com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface.InjFirstPrototypeCandidate;
import com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface.InjPrototypeCandidate;
import com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface.PrototypeCandidate;
import com.bobocode.svydovets.source.autowire.constructor.FirstInjectionCandidate;
import com.bobocode.svydovets.source.autowire.constructor.SecondInjectionCandidate;
import com.bobocode.svydovets.source.autowire.constructor.ValidConstructorInjectionService;
import com.bobocode.svydovets.source.autowire.method.ConfigMethodBasedBeanAutowiring;
import com.bobocode.svydovets.source.beanFactoryTest.throwPrototypeCandidateByInterfaceMoreOnePrimary.InjPrototypeCandidateMoreOnePrimary;
import com.bobocode.svydovets.source.beanFactoryTest.throwPrototypeCandidateByInterfaceWithoutPrimary.InjPrototypeCandidateWithoutPrimary;
import com.bobocode.svydovets.source.circularDependency.CircularDependencyConfig;
import com.bobocode.svydovets.source.circularDependency.FirstCircularDependencyOwner;
import com.bobocode.svydovets.source.circularDependency.SecondCircularDependencyOwner;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import svydovets.exception.NoSuchBeanDefinitionException;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import svydovets.core.context.beanFactory.BeanFactory;
import svydovets.exception.NoUniqueBeanDefinitionException;
import svydovets.exception.UnresolvedCircularDependencyException;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static svydovets.util.ErrorMessageConstants.CIRCULAR_DEPENDENCY_DETECTED;
import static svydovets.util.ErrorMessageConstants.NO_BEAN_DEFINITION_FOUND_OF_TYPE;
import static svydovets.util.ErrorMessageConstants.NO_UNIQUE_BEAN_DEFINITION_FOUND_OF_TYPE;

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

    @Test
    void shouldGetAllRegistersBeansWithoutPrototype() {
        beanFactory.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        Map<String, Object> beans = beanFactory.getBeans();
        assertEquals(2, beans.size());
        assertTrue(beans.containsKey("injFirstCandidate"));
        assertTrue(beans.containsKey("injSecondCandidate"));
    }

    @Test
    void shouldGetPrimaryCandidateByClasTypeForInterface() {
        beanFactory.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        Map<String, Object> beans = beanFactory.getBeans();
        var bean = beanFactory.getBean(InjCandidate.class);
        assertEquals(beans.get("injSecondCandidate"), bean);
    }

    @Test
    void shouldGetPrimaryCandidateByClasTypeAndNameForInterface() {
        beanFactory.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        Map<String, Object> beans = beanFactory.getBeans();
        var bean = beanFactory.getBean("injSecondCandidate", InjCandidate.class);
        assertEquals(beans.get("injSecondCandidate"), bean);
    }

    @Test
    void shouldGetPrototypeCandidateByClasType() {
        beanFactory.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        var bean1 = beanFactory.getBean(PrototypeCandidate.class);
        var bean2 = beanFactory.getBean(PrototypeCandidate.class);
        assertNotEquals(bean1, bean2);
    }

    @Test
    void shouldGetPrototypeCandidateByClasTypeAndName() {
        beanFactory.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        var bean1 = beanFactory.getBean("prototypeCandidate", PrototypeCandidate.class);
        var bean2 = beanFactory.getBean("prototypeCandidate", PrototypeCandidate.class);
        assertNotEquals(bean1, bean2);
    }

    @Test
    void shouldThrowNoSuchBeanDefinitionExceptionGetPrototypeCandidateByClasTypeAndName() {
        beanFactory.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        String errorMessageFormat = "No bean definition found of type %s";
        String errorMessage = String.format(errorMessageFormat, PrototypeCandidate.class.getName());
        var exception = assertThrows(NoSuchBeanDefinitionException.class,
                () -> beanFactory.getBean("prototypeSecondCandidate", PrototypeCandidate.class));
        assertEquals(errorMessage, exception.getMessage());

        errorMessageFormat = "No bean definition found of type %s";
        errorMessage = String.format(errorMessageFormat, InjFirstCandidate.class.getName());
        exception = assertThrows(NoSuchBeanDefinitionException.class,
                () -> beanFactory.getBean("prototypeSecondCandidate", InjFirstCandidate.class));
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void shouldGetBeansOfTypeByRequiredType() {
        beanFactory.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        var expectedBeanMap = beanFactory.getBeans();
        var actualBeanMap = beanFactory.getBeansOfType(InjCandidate.class);

        assertEquals(expectedBeanMap.size(), actualBeanMap.size());
        assertEquals(expectedBeanMap.get("injFirstCandidate"), actualBeanMap.get("injFirstCandidate"));
        assertEquals(expectedBeanMap.get("injSecondCandidate"), actualBeanMap.get("injSecondCandidate"));
    }

    @Test
    void shouldGetPrototypeBeanOfTypeByRequiredTypeAndName() {
        beanFactory.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        var actualBean = beanFactory.getBean("injFirstPrototypeCandidate", InjFirstPrototypeCandidate.class);

        assertEquals(InjFirstPrototypeCandidate.class, actualBean.getClass());
    }

    @Test
    void shouldGetPrototypeBeanOfTypeByRequiredTypeAndName1() {
        beanFactory.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        assertEquals(InjFirstPrototypeCandidate.class, beanFactory.getBean(InjPrototypeCandidate.class).getClass());
    }

    @Test
    void shouldThrowNoSuchBeanDefinitionExceptionWhenGetPrototypeBeanOfTypeWithoutPrimaryByRequiredType() {
        beanFactory.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.throwPrototypeCandidateByInterfaceWithoutPrimary");
        String message = String.format(NO_BEAN_DEFINITION_FOUND_OF_TYPE, InjPrototypeCandidateWithoutPrimary.class.getName());

        assertThatExceptionOfType(NoSuchBeanDefinitionException.class)
                .isThrownBy(() -> beanFactory.getBean(InjPrototypeCandidateWithoutPrimary.class))
                .withMessage(message);
    }

    @Test
    void shouldThrowNoUniqueBeanDefinitionExceptionWhenGetPrototypeBeanOfTypeMoreOnePrimaryByRequiredType() {
        beanFactory.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.throwPrototypeCandidateByInterfaceMoreOnePrimary");
        String message = String.format(NO_UNIQUE_BEAN_DEFINITION_FOUND_OF_TYPE, InjPrototypeCandidateMoreOnePrimary.class.getName());

        assertThatExceptionOfType(NoUniqueBeanDefinitionException.class)
                .isThrownBy(() -> beanFactory.getBean(InjPrototypeCandidateMoreOnePrimary.class))
                .withMessage(message);
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