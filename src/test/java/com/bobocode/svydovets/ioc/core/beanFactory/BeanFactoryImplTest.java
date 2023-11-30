package com.bobocode.svydovets.ioc.core.beanFactory;

import com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface.InjCandidate;
import com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface.InjFirstCandidate;
import com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface.InjFirstPrototypeCandidate;
import com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface.InjPrototypeCandidate;
import com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface.PrototypeCandidate;
import com.bobocode.svydovets.source.autowire.constructor.FirstInjectionCandidate;
import com.bobocode.svydovets.source.autowire.constructor.SecondInjectionCandidate;
import com.bobocode.svydovets.source.autowire.constructor.ValidConstructorInjectionService;
import com.bobocode.svydovets.source.autowire.ConfigMethodBasedBeanAutowiring;
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
import svydovets.core.context.beanFactory.BeanFactoryImpl;
import svydovets.exception.NoUniqueBeanDefinitionException;
import svydovets.exception.UnresolvedCircularDependencyException;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static svydovets.util.ErrorMessageConstants.CIRCULAR_DEPENDENCY_DETECTED;
import static svydovets.util.ErrorMessageConstants.NO_BEAN_DEFINITION_FOUND_OF_TYPE;
import static svydovets.util.ErrorMessageConstants.NO_UNIQUE_BEAN_DEFINITION_FOUND_OF_TYPE;

class BeanFactoryImplTest {

    private BeanFactoryImpl beanFactoryImpl;

    @BeforeEach
    void setUp() {
        this.beanFactoryImpl = new BeanFactoryImpl();
    }


    @Test
    void shouldRegisterBeanWhenConfigClassIsPassed() {
        registerBeanDefinitionsForConfigMethodBaseBeanAutowiring();

        beanFactoryImpl.registerBeans(ConfigMethodBasedBeanAutowiring.class);
        ConfigMethodBasedBeanAutowiring config = beanFactoryImpl.getBean(ConfigMethodBasedBeanAutowiring.class);
        assertThat(config).isNotNull();
    }


    @Test
    void shouldRegisterMethodBasedBeanWhenConfigClassIsPassed() {
        registerBeanDefinitionsForConfigMethodBaseBeanAutowiring();

        beanFactoryImpl.registerBeans(ConfigMethodBasedBeanAutowiring.class);
        ValidConstructorInjectionService injectionService = beanFactoryImpl.getBean(ValidConstructorInjectionService.class);
        assertThat(injectionService).isNotNull();
        assertThat(injectionService.getFirstInjectionCandidate()).isNotNull();
        assertThat(injectionService.getSecondInjectionCandidate()).isNotNull();
    }

    @Test
    void shouldRegisterMethodArgumentBeansWhenConfigClassIsPassed() {
        registerBeanDefinitionsForConfigMethodBaseBeanAutowiring();

        beanFactoryImpl.registerBeans(ConfigMethodBasedBeanAutowiring.class);
        FirstInjectionCandidate firstInjectionCandidate = beanFactoryImpl.getBean(FirstInjectionCandidate.class);
        SecondInjectionCandidate secondInjectionCandidate = beanFactoryImpl.getBean(SecondInjectionCandidate.class);
        assertThat(firstInjectionCandidate).isNotNull();
        assertThat(secondInjectionCandidate).isNotNull();
    }

    @Test
    void shouldRegisterMethodArgumentBeansAndPassThemToMethodBasedBean() {
        registerBeanDefinitionsForConfigMethodBaseBeanAutowiring();

        beanFactoryImpl.registerBeans(ConfigMethodBasedBeanAutowiring.class);
        ValidConstructorInjectionService injectionService = beanFactoryImpl.getBean(ValidConstructorInjectionService.class);
        FirstInjectionCandidate firstInjectionCandidate = beanFactoryImpl.getBean(FirstInjectionCandidate.class);
        SecondInjectionCandidate secondInjectionCandidate = beanFactoryImpl.getBean(SecondInjectionCandidate.class);
        assertThat(injectionService.getFirstInjectionCandidate()).isEqualTo(firstInjectionCandidate);
        assertThat(injectionService.getSecondInjectionCandidate()).isEqualTo(secondInjectionCandidate);
    }

    @Test
    @Disabled
    void shouldThrowExceptionIfCircularDependencyDetectedInClassBasedBeans() {
        AssertionsForClassTypes.assertThatExceptionOfType(UnresolvedCircularDependencyException.class)
                .isThrownBy(() -> beanFactoryImpl.registerBeans(FirstCircularDependencyOwner.class, SecondCircularDependencyOwner.class))
                .withMessage(CIRCULAR_DEPENDENCY_DETECTED, SecondCircularDependencyOwner.class.getName());
    }

    @Test
    @Disabled
    void shouldThrowExceptionIfCircularDependencyDetectedInMethodBasedBeans() {
        AssertionsForClassTypes.assertThatExceptionOfType(UnresolvedCircularDependencyException.class)
                .isThrownBy(() -> beanFactoryImpl.registerBeans(CircularDependencyConfig.class))
                .withMessage(CIRCULAR_DEPENDENCY_DETECTED, SecondCircularDependencyOwner.class.getName());
    }

    @Test
    void shouldGetAllRegistersBeansWithoutPrototype() {
        beanFactoryImpl.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        Map<String, Object> beans = beanFactoryImpl.getBeans();
        assertEquals(2, beans.size());
        assertTrue(beans.containsKey("injFirstCandidate"));
        assertTrue(beans.containsKey("injSecondCandidate"));
    }

    @Test
    void shouldGetPrimaryCandidateByClasTypeForInterface() {
        beanFactoryImpl.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        Map<String, Object> beans = beanFactoryImpl.getBeans();
        var bean = beanFactoryImpl.getBean(InjCandidate.class);
        assertEquals(beans.get("injSecondCandidate"), bean);
    }

    @Test
    void shouldGetPrimaryCandidateByClasTypeAndNameForInterface() {
        beanFactoryImpl.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        Map<String, Object> beans = beanFactoryImpl.getBeans();
        var bean = beanFactoryImpl.getBean("injSecondCandidate", InjCandidate.class);
        assertEquals(beans.get("injSecondCandidate"), bean);
    }

    @Test
    void shouldGetPrototypeCandidateByClasType() {
        beanFactoryImpl.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        var bean1 = beanFactoryImpl.getBean(PrototypeCandidate.class);
        var bean2 = beanFactoryImpl.getBean(PrototypeCandidate.class);
        assertNotEquals(bean1, bean2);
    }

    @Test
    void shouldGetPrototypeCandidateByClasTypeAndName() {
        beanFactoryImpl.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        var bean1 = beanFactoryImpl.getBean("prototypeCandidate", PrototypeCandidate.class);
        var bean2 = beanFactoryImpl.getBean("prototypeCandidate", PrototypeCandidate.class);
        assertNotEquals(bean1, bean2);
    }

    @Test
    void shouldThrowNoSuchBeanDefinitionExceptionGetPrototypeCandidateByClasTypeAndName() {
        beanFactoryImpl.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        String errorMessageFormat = "No bean definition found of type %s";
        String errorMessage = String.format(errorMessageFormat, PrototypeCandidate.class.getName());
        var exception = assertThrows(NoSuchBeanDefinitionException.class,
                () -> beanFactoryImpl.getBean("prototypeSecondCandidate", PrototypeCandidate.class));
        assertEquals(errorMessage, exception.getMessage());

        errorMessageFormat = "No bean definition found of type %s";
        errorMessage = String.format(errorMessageFormat, InjFirstCandidate.class.getName());
        exception = assertThrows(NoSuchBeanDefinitionException.class,
                () -> beanFactoryImpl.getBean("prototypeSecondCandidate", InjFirstCandidate.class));
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void shouldGetBeansOfTypeByRequiredType() {
        beanFactoryImpl.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        var expectedBeanMap = beanFactoryImpl.getBeans();
        var actualBeanMap = beanFactoryImpl.getBeansOfType(InjCandidate.class);

        assertEquals(expectedBeanMap.size(), actualBeanMap.size());
        assertEquals(expectedBeanMap.get("injFirstCandidate"), actualBeanMap.get("injFirstCandidate"));
        assertEquals(expectedBeanMap.get("injSecondCandidate"), actualBeanMap.get("injSecondCandidate"));
    }

    @Test
    void shouldGetPrototypeBeanOfTypeByRequiredTypeAndName() {
        beanFactoryImpl.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        var actualBean = beanFactoryImpl.getBean("injFirstPrototypeCandidate", InjFirstPrototypeCandidate.class);

        assertEquals(InjFirstPrototypeCandidate.class, actualBean.getClass());
    }

    @Test
    void shouldGetPrototypeBeanOfTypeByRequiredTypeAndName1() {
        beanFactoryImpl.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        assertEquals(InjFirstPrototypeCandidate.class, beanFactoryImpl.getBean(InjPrototypeCandidate.class).getClass());
    }

    @Test
    void shouldThrowNoSuchBeanDefinitionExceptionWhenGetPrototypeBeanOfTypeWithoutPrimaryByRequiredType() {
        beanFactoryImpl.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.throwPrototypeCandidateByInterfaceWithoutPrimary");
        String message = String.format(NO_BEAN_DEFINITION_FOUND_OF_TYPE, InjPrototypeCandidateWithoutPrimary.class.getName());

        assertThatExceptionOfType(NoSuchBeanDefinitionException.class)
                .isThrownBy(() -> beanFactoryImpl.getBean(InjPrototypeCandidateWithoutPrimary.class))
                .withMessage(message);
    }

    @Test
    void shouldThrowNoUniqueBeanDefinitionExceptionWhenGetPrototypeBeanOfTypeMoreOnePrimaryByRequiredType() {
        beanFactoryImpl.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.throwPrototypeCandidateByInterfaceMoreOnePrimary");
        String message = String.format(NO_UNIQUE_BEAN_DEFINITION_FOUND_OF_TYPE, InjPrototypeCandidateMoreOnePrimary.class.getName());

        assertThatExceptionOfType(NoUniqueBeanDefinitionException.class)
                .isThrownBy(() -> beanFactoryImpl.getBean(InjPrototypeCandidateMoreOnePrimary.class))
                .withMessage(message);
    }

    private void registerBeanDefinitionsForConfigMethodBaseBeanAutowiring() {
        beanFactoryImpl.beanDefinitionFactory().registerBeanDefinitions(Set.of(
                        ConfigMethodBasedBeanAutowiring.class,
                        ValidConstructorInjectionService.class,
                        FirstInjectionCandidate.class,
                        SecondInjectionCandidate.class
                )
        );
    }
}