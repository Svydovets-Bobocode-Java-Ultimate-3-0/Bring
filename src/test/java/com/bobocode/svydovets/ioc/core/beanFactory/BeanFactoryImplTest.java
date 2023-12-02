package com.bobocode.svydovets.ioc.core.beanFactory;

import com.bobocode.svydovets.source.base.MessageService;
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
import org.junit.jupiter.api.*;
import svydovets.core.exception.BeanCreationException;
import svydovets.core.exception.NoSuchBeanDefinitionException;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import svydovets.core.context.beanFactory.BeanFactoryImpl;
import svydovets.core.exception.NoUniqueBeanDefinitionException;
import svydovets.core.exception.UnresolvedCircularDependencyException;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static svydovets.util.ErrorMessageConstants.CIRCULAR_DEPENDENCY_DETECTED;
import static svydovets.util.ErrorMessageConstants.ERROR_CREATED_BEAN_OF_TYPE;
import static svydovets.util.ErrorMessageConstants.NO_BEAN_DEFINITION_FOUND_OF_TYPE;
import static svydovets.util.ErrorMessageConstants.NO_UNIQUE_BEAN_DEFINITION_FOUND_OF_TYPE;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BeanFactoryImplTest {

    private BeanFactoryImpl beanFactoryImpl;

    @BeforeEach
    void setUp() {
        this.beanFactoryImpl = new BeanFactoryImpl();
    }


    @Test
    @Order(1)
    void shouldRegisterBeanWhenConfigClassIsPassed() {
        registerBeanDefinitionsForConfigMethodBaseBeanAutowiring();

        beanFactoryImpl.registerBeans(ConfigMethodBasedBeanAutowiring.class);
        ConfigMethodBasedBeanAutowiring config = beanFactoryImpl.getBean(ConfigMethodBasedBeanAutowiring.class);
        assertThat(config).isNotNull();
    }


    @Test
    @Order(2)
    void shouldRegisterMethodBasedBeanWhenConfigClassIsPassed() {
        registerBeanDefinitionsForConfigMethodBaseBeanAutowiring();

        beanFactoryImpl.registerBeans(ConfigMethodBasedBeanAutowiring.class);
        ValidConstructorInjectionService injectionService = beanFactoryImpl.getBean(ValidConstructorInjectionService.class);
        assertThat(injectionService).isNotNull();
        assertThat(injectionService.getFirstInjectionCandidate()).isNotNull();
        assertThat(injectionService.getSecondInjectionCandidate()).isNotNull();
    }

    @Test
    @Order(3)
    void shouldRegisterMethodArgumentBeansWhenConfigClassIsPassed() {
        registerBeanDefinitionsForConfigMethodBaseBeanAutowiring();

        beanFactoryImpl.registerBeans(ConfigMethodBasedBeanAutowiring.class);
        FirstInjectionCandidate firstInjectionCandidate = beanFactoryImpl.getBean(FirstInjectionCandidate.class);
        SecondInjectionCandidate secondInjectionCandidate = beanFactoryImpl.getBean(SecondInjectionCandidate.class);
        assertThat(firstInjectionCandidate).isNotNull();
        assertThat(secondInjectionCandidate).isNotNull();
    }

    @Test
    @Order(4)
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
    @Order(5)
    void shouldThrowExceptionIfCircularDependencyDetectedInClassBasedBeans() {
        AssertionsForClassTypes.assertThatExceptionOfType(BeanCreationException.class)
                .isThrownBy(() -> beanFactoryImpl.registerBeans(FirstCircularDependencyOwner.class, SecondCircularDependencyOwner.class));
    }

    @Test
    @Order(6)
    void shouldThrowExceptionIfCircularDependencyDetectedInMethodBasedBeans() {
        AssertionsForClassTypes.assertThatExceptionOfType(BeanCreationException.class)
                .isThrownBy(() -> beanFactoryImpl.registerBeans(CircularDependencyConfig.class))
                .withMessage(ERROR_CREATED_BEAN_OF_TYPE, MessageService.class.getName());
    }

    @Test
    @Order(7)
    void shouldGetAllRegistersBeansWithoutPrototype() {
        beanFactoryImpl.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        Map<String, Object> beans = beanFactoryImpl.getBeans();
        assertEquals(2, beans.size());
        assertTrue(beans.containsKey("injFirstCandidate"));
        assertTrue(beans.containsKey("injSecondCandidate"));
    }

    @Test
    @Order(8)
    void shouldGetPrimaryCandidateByClasTypeForInterface() {
        beanFactoryImpl.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        Map<String, Object> beans = beanFactoryImpl.getBeans();
        var bean = beanFactoryImpl.getBean(InjCandidate.class);
        assertEquals(beans.get("injSecondCandidate"), bean);
    }

    @Test
    @Order(9)
    void shouldGetPrimaryCandidateByClasTypeAndNameForInterface() {
        beanFactoryImpl.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        Map<String, Object> beans = beanFactoryImpl.getBeans();
        var bean = beanFactoryImpl.getBean("injSecondCandidate", InjCandidate.class);
        assertEquals(beans.get("injSecondCandidate"), bean);
    }

    @Test
    @Order(10)
    void shouldGetPrototypeCandidateByClasType() {
        beanFactoryImpl.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        var bean1 = beanFactoryImpl.getBean(PrototypeCandidate.class);
        var bean2 = beanFactoryImpl.getBean(PrototypeCandidate.class);
        assertNotEquals(bean1, bean2);
    }

    @Test
    @Order(11)
    void shouldGetPrototypeCandidateByClasTypeAndName() {
        beanFactoryImpl.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        var bean1 = beanFactoryImpl.getBean("prototypeCandidate", PrototypeCandidate.class);
        var bean2 = beanFactoryImpl.getBean("prototypeCandidate", PrototypeCandidate.class);
        assertNotEquals(bean1, bean2);
    }

    @Test
    @Order(12)
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
    @Order(13)
    void shouldGetBeansOfTypeByRequiredType() {
        beanFactoryImpl.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        var expectedBeanMap = beanFactoryImpl.getBeans();
        var actualBeanMap = beanFactoryImpl.getBeansOfType(InjCandidate.class);

        assertEquals(expectedBeanMap.size(), actualBeanMap.size());
        assertEquals(expectedBeanMap.get("injFirstCandidate"), actualBeanMap.get("injFirstCandidate"));
        assertEquals(expectedBeanMap.get("injSecondCandidate"), actualBeanMap.get("injSecondCandidate"));
    }

    @Test
    @Order(14)
    void shouldGetPrototypeBeanOfTypeByRequiredTypeAndName() {
        beanFactoryImpl.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        var actualBean = beanFactoryImpl.getBean("injFirstPrototypeCandidate", InjFirstPrototypeCandidate.class);

        assertEquals(InjFirstPrototypeCandidate.class, actualBean.getClass());
    }

    @Test
    @Order(15)
    void shouldGetPrototypeBeanOfTypeByRequiredTypeAndName1() {
        beanFactoryImpl.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        assertEquals(InjFirstPrototypeCandidate.class, beanFactoryImpl.getBean(InjPrototypeCandidate.class).getClass());
    }

    @Test
    @Order(16)
    void shouldThrowNoSuchBeanDefinitionExceptionWhenGetPrototypeBeanOfTypeWithoutPrimaryByRequiredType() {
        beanFactoryImpl.registerBeans("com.bobocode.svydovets.source.beanFactoryTest.throwPrototypeCandidateByInterfaceWithoutPrimary");
        String message = String.format(NO_BEAN_DEFINITION_FOUND_OF_TYPE, InjPrototypeCandidateWithoutPrimary.class.getName());

        assertThatExceptionOfType(NoSuchBeanDefinitionException.class)
                .isThrownBy(() -> beanFactoryImpl.getBean(InjPrototypeCandidateWithoutPrimary.class))
                .withMessage(message);
    }

    @Test
    @Order(17)
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