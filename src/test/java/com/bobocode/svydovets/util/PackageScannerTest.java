package com.bobocode.svydovets.util;

import com.bobocode.svydovets.beansForTestScan.SomeBean;
import com.bobocode.svydovets.beansForTestScan.SomeBeanSecond;
import com.bobocode.svydovets.config.BeanConfigBase;
import com.bobocode.svydovets.config.BeanConfigPostConstruct;
import com.bobocode.svydovets.config.NonAnnotatedClass;
import com.bobocode.svydovets.service.base.CollectionsHolderService;
import com.bobocode.svydovets.service.base.CommonService;
import com.bobocode.svydovets.service.base.EditService;
import com.bobocode.svydovets.service.base.MessageService;
import com.bobocode.svydovets.service.postconstruct.invalid.DuplicatePostConstructService;
import com.bobocode.svydovets.service.postconstruct.valid.PostConstructService;
import com.bobocode.svydovets.someconfig.BeanConfigForSomeBeans;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import svydovets.util.PackageScanner;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PackageScannerTest {
    private final PackageScanner packageScanner = new PackageScanner();
    private final Set<Class<?>> beansScannedClasses = Set.of(CollectionsHolderService.class, CommonService.class,
            EditService.class, MessageService.class, DuplicatePostConstructService.class, PostConstructService.class,
            CollectionsHolderService.ListHolderService.class, CollectionsHolderService.SetHolderService.class,
            CollectionsHolderService.MapHolderService.class, BeanConfigBase.class, BeanConfigPostConstruct.class);

    private final Set<Class<?>> beansScannedClassesWithoutConfigs = Set.of(CollectionsHolderService.class, CommonService.class,
            EditService.class, MessageService.class, DuplicatePostConstructService.class, PostConstructService.class,
            CollectionsHolderService.ListHolderService.class, CollectionsHolderService.SetHolderService.class,
            CollectionsHolderService.MapHolderService.class);

    private final Set<Class<?>> beansScannedByConfigClass = Set.of(CollectionsHolderService.class, CommonService.class,
            EditService.class, MessageService.class, CollectionsHolderService.ListHolderService.class, CollectionsHolderService.SetHolderService.class,
            CollectionsHolderService.MapHolderService.class);

    private final Set<Class<?>> beansScannedClasses2 = Set.of(BeanConfigForSomeBeans.class, SomeBean.class, SomeBeanSecond.class);

    // todo: Implement tests for all methods of PackageScanner()
    @Test
    void testFindAllBeanByBasePackage() {
        Set<?> beanClasses = packageScanner.findAllBeanByBasePackage("com.bobocode.svydovets.config");
        assertThat(beanClasses.size()).isEqualTo(11);
        assertThat(beanClasses).isEqualTo(beansScannedClasses);
    }

    @Test
    void testFindAllBeanByBasePackageOuter() {
        Set<?> beanClasses = packageScanner.findAllBeanByBasePackage("com.bobocode.svydovets.someconfig");
        assertThat(beanClasses.size()).isEqualTo(3);
        assertThat(beanClasses).isEqualTo(beansScannedClasses2);
    }

    @Test
    void beansShouldNotContainNonAnnotated() {
        Set<?> beanClasses = packageScanner.findAllBeanByBasePackage("com.bobocode.svydovets.config");
        assertThat(beanClasses.contains(NonAnnotatedClass.class)).isFalse();
    }

    @Test
    void testFindAllBeanByBasePackageShouldBeEmpty() {
        Set<?> beanClasses = packageScanner.findAllBeanByBasePackage("com.bobocode.svydovets.withoutAnnotation");
        assertThat(beanClasses.isEmpty()).isTrue();
    }

    @Test
    void testScanAllBeansByConfigClass() {
        Set<Class<?>> configClasses = Set.of(BeanConfigBase.class, BeanConfigPostConstruct.class);
        Set<Class<?>> result = new HashSet<>();

        packageScanner.scanAllBeansByConfigClass(configClasses, result);

        assertThat(result.size()).isEqualTo(beansScannedClassesWithoutConfigs.size());
        assertThat(result).isEqualTo(beansScannedClassesWithoutConfigs);
    }

    @Test
    void testFindAllBeanByBaseClass() {
        Class<?> baseClass = BeanConfigBase.class;
        Set<Class<?>> result = packageScanner.findAllBeanByBaseClass(baseClass);

        assertThat(result.size()).isEqualTo(beansScannedByConfigClass.size());
        assertThat(result).isEqualTo(beansScannedByConfigClass);
    }
}
