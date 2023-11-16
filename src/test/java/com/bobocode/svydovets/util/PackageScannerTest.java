package com.bobocode.svydovets.util;

import com.bobocode.svydovets.beansForTestScan.SomeBean;
import com.bobocode.svydovets.beansForTestScan.SomeBeanSecond;
import com.bobocode.svydovets.config.BeanConfigBase;
import com.bobocode.svydovets.config.BeanConfigPostConstruct;
import com.bobocode.svydovets.withoutAnnotation.NonAnnotatedClass;
import com.bobocode.svydovets.service.base.CollectionsHolderService;
import com.bobocode.svydovets.service.base.CommonService;
import com.bobocode.svydovets.service.base.EditService;
import com.bobocode.svydovets.service.base.MessageService;
import com.bobocode.svydovets.service.postconstruct.invalid.DuplicatePostConstructService;
import com.bobocode.svydovets.service.postconstruct.valid.PostConstructService;
import com.bobocode.svydovets.config.BeanConfigForPackageScanner;
import com.bobocode.svydovets.withoutAnnotation.NonAnnotatedClassFirst;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import svydovets.util.PackageScanner;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PackageScannerTest {
    private final PackageScanner packageScanner = new PackageScanner();

    private final Set<Class<?>> beansScannedClassesWithoutConfigs = Set.of(CollectionsHolderService.class, CommonService.class,
            EditService.class, MessageService.class, DuplicatePostConstructService.class, PostConstructService.class,
            CollectionsHolderService.ListHolderService.class, CollectionsHolderService.SetHolderService.class,
            CollectionsHolderService.MapHolderService.class);

    private final Set<Class<?>> beansScannedByConfigClass = Set.of(CollectionsHolderService.class, CommonService.class,
            EditService.class, MessageService.class, CollectionsHolderService.ListHolderService.class, CollectionsHolderService.SetHolderService.class,
            CollectionsHolderService.MapHolderService.class);

    private final Set<Class<?>> beansScannedByConfigAndOtherClasses = Set.of(CollectionsHolderService.class, CommonService.class,
            EditService.class, MessageService.class, CollectionsHolderService.ListHolderService.class, CollectionsHolderService.SetHolderService.class,
            CollectionsHolderService.MapHolderService.class, SomeBean.class, PostConstructService.class);
    private final Set<Class<?>> beansScannedClasses2 = Set.of(SomeBean.class, SomeBeanSecond.class);


    @Test
    void testScanAllBeansByConfigClass() {
        Set<Class<?>> result = packageScanner.findAllBeanCandidatesByBaseClass(BeanConfigBase.class, BeanConfigPostConstruct.class);

        assertThat(result.size()).isEqualTo(beansScannedClassesWithoutConfigs.size());
        assertThat(result).isEqualTo(beansScannedClassesWithoutConfigs);
    }

    @Test
    void testFindAllBeanByBaseClass() {
        Set<Class<?>> result = packageScanner.findAllBeanCandidatesByBaseClass(BeanConfigBase.class);

        assertThat(result.size()).isEqualTo(beansScannedByConfigClass.size());
        assertThat(result).isEqualTo(beansScannedByConfigClass);
    }

    @Test
    void testFindAllBeanByBaseClassOuterPackage() {
        Set<Class<?>> result = packageScanner.findAllBeanCandidatesByBaseClass(BeanConfigForPackageScanner.class);

        assertThat(result.size()).isEqualTo(beansScannedClasses2.size());
        assertThat(result).isEqualTo(beansScannedClasses2);
    }

    @Test
    void testFindAllBeanByBaseClassShouldReturnEmptySet() {
        Set<Class<?>> result = packageScanner.findAllBeanCandidatesByBaseClass(NonAnnotatedClass.class);

        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void testFindAllBeanByBaseConfigWithOtherClasses() {
        Set<Class<?>> result = packageScanner.findAllBeanCandidatesByBaseClass(BeanConfigBase.class, SomeBean.class,
                PostConstructService.class, NonAnnotatedClassFirst.class);

        assertThat(result).isEqualTo(beansScannedByConfigAndOtherClasses);
        assertThat(result.contains(NonAnnotatedClassFirst.class)).isFalse();
        assertThat(result.contains(PostConstructService.class)).isTrue();
    }
}
