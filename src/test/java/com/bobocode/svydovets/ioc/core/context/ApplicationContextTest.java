package com.bobocode.svydovets.ioc.core.context;

import com.bobocode.svydovets.source.autowire.method.TrimService;
import com.bobocode.svydovets.source.base.CommonService;
import com.bobocode.svydovets.source.base.MessageService;
import com.bobocode.svydovets.source.base.NullService;
import com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface.InjCandidate;
import com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface.InjFirstCandidate;
import com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface.InjFirstPrototypeCandidate;
import com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface.InjPrototypeCandidate;
import com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface.PrototypeCandidate;
import com.bobocode.svydovets.source.beanFactoryTest.throwPrototypeCandidateByInterfaceMoreOnePrimary.InjPrototypeCandidateMoreOnePrimary;
import com.bobocode.svydovets.source.beanFactoryTest.throwPrototypeCandidateByInterfaceWithoutPrimary.InjPrototypeCandidateWithoutPrimary;
import com.bobocode.svydovets.source.config.BasePackageBeansConfig;
import com.bobocode.svydovets.source.config.PrimaryPackageBeansConfig;
import com.bobocode.svydovets.source.config.QualifierPackageBeansConfig;
import com.bobocode.svydovets.source.primary.PrimaryService;
import com.bobocode.svydovets.source.qualifier.valid.GroceryItem;
import com.bobocode.svydovets.source.qualifier.valid.OrderService;
import com.bobocode.svydovets.source.qualifier.valid.StoreItem;
import com.bobocode.svydovets.source.qualifier.withoutPrimary.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import svydovets.core.context.AnnotationConfigApplicationContext;
import svydovets.core.context.ApplicationContext;
import svydovets.core.exception.NoSuchBeanDefinitionException;
import svydovets.core.exception.NoUniqueBeanDefinitionException;
import svydovets.core.exception.NoUniqueBeanException;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static svydovets.util.ErrorMessageConstants.NO_BEAN_DEFINITION_FOUND_OF_TYPE;
import static svydovets.util.ErrorMessageConstants.NO_UNIQUE_BEAN_DEFINITION_FOUND_OF_TYPE;
import static svydovets.util.ErrorMessageConstants.NO_UNIQUE_BEAN_FOUND_OF_TYPE;


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
    void shouldThrowNoSuchBeanDefinitionExceptionWhenBeanIsNotPresent() {
        ApplicationContext context = new AnnotationConfigApplicationContext(BasePackageBeansConfig.class);
        assertThatExceptionOfType(NoSuchBeanDefinitionException.class)
                .isThrownBy(() -> context.getBean(TrimService.class))
                .withMessage(String.format(NO_BEAN_DEFINITION_FOUND_OF_TYPE, TrimService.class.getName()));
    }

    @Test
    @Order(6)
    void shouldThrowNoUniqueBeanExceptionWhenUniqueBeanIsNotPresent() {
        ApplicationContext context = new AnnotationConfigApplicationContext(QualifierPackageBeansConfig.class);
        assertThatExceptionOfType(NoUniqueBeanException.class)
                .isThrownBy(() -> context.getBean(PaymentService.class))
                .withMessage(String.format(NO_UNIQUE_BEAN_FOUND_OF_TYPE, PaymentService.class.getName()));
    }

    @Test
    @Order(7)
    void shouldThrowNoSuchBeanDefinitionExceptionWhenBeanIsNotPresentByName() {
        ApplicationContext context = new AnnotationConfigApplicationContext(BasePackageBeansConfig.class);
        assertThatExceptionOfType(NoSuchBeanDefinitionException.class)
                .isThrownBy(() -> context.getBean("superMessageService", MessageService.class))
                .withMessage(String.format(NO_BEAN_DEFINITION_FOUND_OF_TYPE, MessageService.class.getName()));
    }

    @Test
    @Order(8)
    void shouldThrowNoSuchBeanExceptionWhenBeanIsPresentByNameButHasDifferentClassType() {
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
    void shouldReturnBeanByNameAndClassTypeIfBeanIsPresent() {
        ApplicationContext context = new AnnotationConfigApplicationContext(BasePackageBeansConfig.class);
        MessageService messageService = context.getBean("messageService", MessageService.class);
        assertThat(messageService).isNotNull();
    }

    @Test
    @Order(10)
    void shouldThrowNoUniqueBeanExceptionByBeanClassIfTwoPrimaryBeansArePresent() {
        ApplicationContext context = new AnnotationConfigApplicationContext(PrimaryPackageBeansConfig.class);
        assertThatExceptionOfType(NoUniqueBeanException.class)
                .isThrownBy(() -> context.getBean(PrimaryService.class))
                .withMessage(String.format(
                        NO_UNIQUE_BEAN_FOUND_OF_TYPE,
                        PrimaryService.class.getName())
                );
    }

    @Test
    @Order(11)
    void shouldReturnBeanByNameAndClassIfTwoPrimaryBeansArePresent() {
        ApplicationContext context = new AnnotationConfigApplicationContext(PrimaryPackageBeansConfig.class);
        PrimaryService firstPrimaryServiceImpl = context.getBean("firstPrimaryServiceImpl", PrimaryService.class);
        assertThat(firstPrimaryServiceImpl).isNotNull();
    }

    @Test
    @Order(12)
    void shouldGetAllRegistersBeansWithoutPrototype() {
        ApplicationContext context = new AnnotationConfigApplicationContext("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        Map<String, Object> beans = context.getBeans();
        assertEquals(2, beans.size());
        assertTrue(beans.containsKey("injFirstCandidate"));
        assertTrue(beans.containsKey("injSecondCandidate"));
    }

    @Test
    @Order(13)
    void shouldGetPrimaryCandidateByClasTypeForInterface() {
        ApplicationContext context = new AnnotationConfigApplicationContext("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        Map<String, Object> beans = context.getBeans();
        var bean = context.getBean(InjCandidate.class);
        assertEquals(bean, beans.get("injSecondCandidate"));
    }

    @Test
    @Order(14)
    void shouldGetPrimaryCandidateByClasTypeAndNameForInterface() {
        ApplicationContext context = new AnnotationConfigApplicationContext("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        Map<String, Object> beans = context.getBeans();
        var bean = context.getBean("injSecondCandidate", InjCandidate.class);
        assertEquals(bean, beans.get("injSecondCandidate"));
    }

    @Test
    @Order(15)
    void shouldGetPrototypeCandidateByClasType() {
        ApplicationContext context = new AnnotationConfigApplicationContext("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        var bean1 = context.getBean(PrototypeCandidate.class);
        var bean2 = context.getBean(PrototypeCandidate.class);
        assertNotEquals(bean1, bean2);
    }

    @Test
    @Order(16)
    void shouldGetPrototypeCandidateByClasTypeAndName() {
        ApplicationContext context = new AnnotationConfigApplicationContext("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        var bean1 = context.getBean("prototypeCandidate", PrototypeCandidate.class);
        var bean2 = context.getBean("prototypeCandidate", PrototypeCandidate.class);
        assertNotEquals(bean1, bean2);
    }

    @Test
    @Order(17)
    void shouldThrowNoSuchBeanDefinitionExceptionGetPrototypeCandidateByClasTypeAndName() {
        ApplicationContext context = new AnnotationConfigApplicationContext("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        String errorMessageFormat = "No bean definition found of type %s";
        String errorMessage = String.format(errorMessageFormat, PrototypeCandidate.class.getName());
        var exception = assertThrows(NoSuchBeanDefinitionException.class,
                () -> context.getBean("prototypeSecondCandidate", PrototypeCandidate.class));
        assertEquals(errorMessage, exception.getMessage());

        errorMessageFormat = "No bean definition found of type %s";
        errorMessage = String.format(errorMessageFormat, InjFirstCandidate.class.getName());
        exception = assertThrows(NoSuchBeanDefinitionException.class,
                () -> context.getBean("prototypeSecondCandidate", InjFirstCandidate.class));
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @Order(18)
    void shouldGetBeansOfTypeByRequiredType() {
        ApplicationContext context = new AnnotationConfigApplicationContext("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        var expectedBeanMap = context.getBeans();
        var actualBeanMap = context.getBeansOfType(InjCandidate.class);

        assertEquals(expectedBeanMap.size(), actualBeanMap.size());
        assertEquals(expectedBeanMap.get("injFirstCandidate"), actualBeanMap.get("injFirstCandidate"));
        assertEquals(expectedBeanMap.get("injSecondCandidate"), actualBeanMap.get("injSecondCandidate"));
    }

    @Test
    @Order(19)
    void shouldGetPrototypeBeanOfTypeByRequiredTypeAndName() {
        ApplicationContext context = new AnnotationConfigApplicationContext("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        var actualBean = context.getBean("injFirstPrototypeCandidate", InjFirstPrototypeCandidate.class);

        assertEquals(InjFirstPrototypeCandidate.class, actualBean.getClass());
    }

    @Test
    @Order(20)
    void shouldGetPrototypeBeanOfTypeByRequiredTypeAndName1() {
        ApplicationContext context = new AnnotationConfigApplicationContext("com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface");
        var actualBean = context.getBean(InjPrototypeCandidate.class);

        assertEquals(InjFirstPrototypeCandidate.class, actualBean.getClass());
    }

    @Test
    @Order(21)
    void shouldThrowNoSuchBeanDefinitionExceptionWhenGetPrototypeBeanOfTypeWithoutPrimaryByRequiredType() {
        ApplicationContext context = new AnnotationConfigApplicationContext("com.bobocode.svydovets.source.beanFactoryTest.throwPrototypeCandidateByInterfaceWithoutPrimary");
        String message = String.format(NO_BEAN_DEFINITION_FOUND_OF_TYPE, InjPrototypeCandidateWithoutPrimary.class.getName());

        assertThatExceptionOfType(NoSuchBeanDefinitionException.class)
                .isThrownBy(() -> context.getBean(InjPrototypeCandidateWithoutPrimary.class))
                .withMessage(message);
    }

    @Test
    @Order(22)
    void shouldThrowNoUniqueBeanDefinitionExceptionWhenGetPrototypeBeanOfTypeMoreOnePrimaryByRequiredType() {
        ApplicationContext context = new AnnotationConfigApplicationContext("com.bobocode.svydovets.source.beanFactoryTest.throwPrototypeCandidateByInterfaceMoreOnePrimary");
        String message = String.format(NO_UNIQUE_BEAN_DEFINITION_FOUND_OF_TYPE, InjPrototypeCandidateMoreOnePrimary.class.getName());

        assertThatExceptionOfType(NoUniqueBeanDefinitionException.class)
                .isThrownBy(() -> context.getBean(InjPrototypeCandidateMoreOnePrimary.class))
                .withMessage(message);
    }

    @Test
    @Order(23)
    void shouldInjectFieldThatIsAnnotatedQualifier() {
        ApplicationContext context = new AnnotationConfigApplicationContext("com.bobocode.svydovets.source.qualifier.valid");

        OrderService orderService = context.getBean(OrderService.class);

        assertNotNull(orderService.getItem());
        assertEquals(StoreItem.class, orderService.getItem().getClass());
    }

    @Test
    @Order(24)
    void shouldInjectFieldToMethodThatIsAnnotatedQualifier() {
        ApplicationContext context = new AnnotationConfigApplicationContext("com.bobocode.svydovets.source.qualifier.valid");

        OrderService orderService = context.getBean(OrderService.class);

        assertNotNull(orderService.getSecondItem());
        assertEquals(GroceryItem.class, orderService.getSecondItem().getClass());
    }

    @Test
    @Order(25)
    void shouldThrowExceptionIfNameInQualifierIsNotCorrect() {
        String expectedMessage = "No bean found of type interface com.bobocode.svydovets.source.qualifier.invalid.InvalidItem by name storeItem";

        assertThatExceptionOfType(NoSuchBeanDefinitionException.class)
                .isThrownBy(() ->  new AnnotationConfigApplicationContext("com.bobocode.svydovets.source.qualifier.invalid"))
                .withMessage(expectedMessage);
    }

}
