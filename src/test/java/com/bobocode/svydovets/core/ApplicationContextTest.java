package com.bobocode.svydovets.core;

import com.bobocode.svydovets.config.BeanConfig;
import com.bobocode.svydovets.service.NonPrimaryProductServiceImpl;
import com.bobocode.svydovets.service.PrimaryProductServiceImpl;
import com.bobocode.svydovets.service.ProductService;
import com.bobocode.svydovets.service.TrimService;
import com.bobocode.svydovets.service.base.CollectionsHolderService;
import com.bobocode.svydovets.service.base.EditService;
import com.bobocode.svydovets.service.base.MessageService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import svydovets.core.context.ApplicationContext;
import svydovets.core.context.DefaultApplicationContext;
import svydovets.exception.NoSuchBeanException;
import svydovets.exception.NoUniqueBeanException;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApplicationContextTest {

    @Test
    void createApplicationContextFromBasePackage() {
        ApplicationContext context = new DefaultApplicationContext("com.bobocode.svydovets.service.base");
        assertThat(context.getBean(MessageService.class)).isNotNull();
        assertThat(context.getBean(EditService.class)).isNotNull();
        // todo: It may be better to group the existing "services" in additional packages and check that all beans of
        //  specified "basePackage" are created
    }


    @Test
    void getBeanThrowNoSuchBeanException() {
        ApplicationContext context = new DefaultApplicationContext("com.bobocode.svydovets.service.base");
        assertThatExceptionOfType(NoSuchBeanException.class)
                .isThrownBy(() -> context.getBean(TrimService.class))
                .withMessage(String.format("No bean found of type %s", TrimService.class.getName()));
    }

    @Test
    void getBeanThrowNoUniqueBeanException() {
        ApplicationContext context = new DefaultApplicationContext("com.bobocode.svydovets.service");
        assertThatExceptionOfType(NoUniqueBeanException.class)
                .isThrownBy(() -> context.getBean(ProductService.class))
                .withMessage(String.format("No unique bean found of type %s", ProductService.class.getName()));
    }

    @Test
    void injectBeansToTheList() {
        ApplicationContext context = new DefaultApplicationContext("com.bobocode.svydovets.service");

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
    void injectBeansToTheSet() {
        ApplicationContext context = new DefaultApplicationContext("com.bobocode.svydovets.service");

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
