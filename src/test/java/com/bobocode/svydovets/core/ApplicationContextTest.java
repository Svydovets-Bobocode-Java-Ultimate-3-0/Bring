package com.bobocode.svydovets.core;

import com.bobocode.svydovets.config.BeanConfigBase;
import com.bobocode.svydovets.service.TrimService;
import com.bobocode.svydovets.service.base.CollectionsHolderService;
import com.bobocode.svydovets.service.base.CommonService;
import com.bobocode.svydovets.service.base.EditService;
import com.bobocode.svydovets.service.base.MessageService;
import com.bobocode.svydovets.service.qualifier.NonPrimaryProductServiceImpl;
import com.bobocode.svydovets.service.qualifier.PrimaryProductServiceImpl;
import com.bobocode.svydovets.service.qualifier.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import svydovets.core.context.AnnotationConfigApplicationContext;
import svydovets.core.context.ApplicationContext;
import svydovets.exception.NoSuchBeanException;
import svydovets.exception.NoUniqueBeanException;
import svydovets.util.PackageScanner;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@Disabled
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApplicationContextTest {
    @Mock
    private PackageScanner packageScanner;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    @Order(1)
    void createApplicationContextFromBasePackage() {
        ApplicationContext context = new AnnotationConfigApplicationContext("com.bobocode.svydovets.service.base");
        assertThat(context).isNotNull();
    }

    @Test
    @Order(2)
    void createApplicationContextFromConfigClass() {
        ApplicationContext context = new AnnotationConfigApplicationContext(BeanConfigBase.class);
        assertThat(context).isNotNull();
    }

    @Test
    @Order(3)
    void applicationContextFromBasePackageCreatesAllRequiredBeans() {
        String basePackage = "com.bobocode.svydovets.service.base";

        when(packageScanner.findComponentsByBasePackage(basePackage))
                .thenReturn(Set.of(CommonService.class, EditService.class, MessageService.class));

        ApplicationContext context = new AnnotationConfigApplicationContext(basePackage);

        assertThat(context.getBean(CommonService.class)).isNotNull();
        assertThat(context.getBean(MessageService.class)).isNotNull();
        assertThat(context.getBean(EditService.class)).isNotNull();
    }

    @Test
    @Order(4)
    void applicationContextFromConfigClassCreatesAllRequiredBeans() {
        when(packageScanner.findAllBeanCandidatesByBaseClass(BeanConfigBase.class))
                .thenReturn(Set.of(BeanConfigBase.class, TrimService.class, MessageService.class));

        ApplicationContext context = new AnnotationConfigApplicationContext(BeanConfigBase.class);

        assertThat(context.getBean(BeanConfigBase.class)).isNotNull();
        assertThat(context.getBean(TrimService.class)).isNotNull();
        assertThat(context.getBean(MessageService.class)).isNotNull();
    }


    @Test
    @Order(5)
    void getBeanThrowNoSuchBeanException() {
        String basePackage = "com.bobocode.svydovets.service.qualifier";
        ApplicationContext context = new AnnotationConfigApplicationContext(basePackage);
        when(packageScanner.findComponentsByBasePackage(basePackage))
                .thenReturn(Set.of(CommonService.class, EditService.class, MessageService.class));
        assertThatExceptionOfType(NoSuchBeanException.class)
                .isThrownBy(() -> context.getBean(TrimService.class))
                .withMessage(String.format("No bean found of type %s", TrimService.class.getName()));
    }

    @Test
    @Order(6)
    void getBeanThrowNoUniqueBeanException() {
        String basePackage = "com.bobocode.svydovets.service.qualifier";
        ApplicationContext context = new AnnotationConfigApplicationContext(basePackage);
        when(packageScanner.findComponentsByBasePackage(basePackage))
                .thenReturn(Set.of(PrimaryProductServiceImpl.class, NonPrimaryProductServiceImpl.class));
        assertThatExceptionOfType(NoUniqueBeanException.class)
                .isThrownBy(() -> context.getBean(ProductService.class))
                .withMessage(String.format("No unique bean found of type %s", ProductService.class.getName()));
    }

    @Test
    @Order(7)
    void injectBeansToTheList() {
        when(packageScanner.findComponentsByBasePackage("com.bobocode.svydovets.service"))
                .thenReturn(Set.of(CollectionsHolderService.ListHolderService.class, NonPrimaryProductServiceImpl.class, PrimaryProductServiceImpl.class));

        ApplicationContext context = new AnnotationConfigApplicationContext("com.bobocode.svydovets.service");

        CollectionsHolderService.ListHolderService listHolderService = context.getBean(CollectionsHolderService.ListHolderService.class);
        var productServiceList = listHolderService.getProductServiceList();
        assertThat(productServiceList).isNotNull();
        assertThat(productServiceList.size()).isEqualTo(2);

        List<ProductService> injectedBeansSortedByName = listHolderService.getProductServiceList()
                .stream()
                .sorted(Comparator.comparing(service -> service.getClass().getName()))
                .toList();

        assertThat(injectedBeansSortedByName.get(0).getClass()).isEqualTo(NonPrimaryProductServiceImpl.class);
        assertThat(injectedBeansSortedByName.get(1).getClass()).isEqualTo(PrimaryProductServiceImpl.class);
    }

    @Test
    @Order(8)
    void injectBeansToTheSet() {
        ApplicationContext context = new AnnotationConfigApplicationContext("com.bobocode.svydovets.service.base");

        CollectionsHolderService.SetHolderService setHolderService = context.getBean(CollectionsHolderService.SetHolderService.class);
        var productServiceList = setHolderService.getProductServiceSet();
        assertThat(productServiceList).isNotNull();
        assertThat(productServiceList.size()).isEqualTo(2);

        List<ProductService> injectedBeansSortedByName = setHolderService.getProductServiceSet()
                .stream()
                .sorted(Comparator.comparing(service -> service.getClass().getName()))
                .toList();

        assertThat(injectedBeansSortedByName.get(0).getClass()).isEqualTo(NonPrimaryProductServiceImpl.class);
        assertThat(injectedBeansSortedByName.get(1).getClass()).isEqualTo(PrimaryProductServiceImpl.class);
    }


    // todo: 1) Add tests for "getBean(String name, Class<T> requiredType)" method
    // todo: 2) Add tests for "getBeansOfType(Class<T> requiredType)" method
    // todo: 3) Add tests for "getPreparedNoArgsConstructor()" method.
    //  3.1) Create a class with no default constructor.
    //  Problem is that other tests failed, because this class is scanned by "Reflections" even if it is in nested package

}
