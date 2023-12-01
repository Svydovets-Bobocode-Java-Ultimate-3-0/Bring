package com.bobocode.svydovets.ioc.util;

import com.bobocode.svydovets.source.base.CommonService;
import com.bobocode.svydovets.source.base.MessageService;
import com.bobocode.svydovets.source.base.NullService;
import com.bobocode.svydovets.source.beansForTestScan.SomeBean;
import com.bobocode.svydovets.source.beansForTestScan.SomeBeanSecond;
import com.bobocode.svydovets.source.config.BasePackageBeansConfig;
import com.bobocode.svydovets.source.config.PackageScannerBeansConfig;
import com.bobocode.svydovets.source.config.PostConstructBeansConfig;
import com.bobocode.svydovets.source.interfaces.NonInterfaceComponent;
import com.bobocode.svydovets.source.interfaces.NonInterfaceConfiguration;
import com.bobocode.svydovets.source.postconstruct.invalid.DuplicatePostConstructService;
import com.bobocode.svydovets.source.postconstruct.valid.PostConstructService;
import com.bobocode.svydovets.source.withoutAnnotation.NonAnnotatedClass;
import com.bobocode.svydovets.source.withoutAnnotation.NonAnnotatedClassFirst;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import svydovets.util.PackageScanner;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PackageScannerTest {
    private final PackageScanner packageScanner = new PackageScanner();

    private final Set<Class<?>> beansScannedClassesWithoutInterfaces = Set.of(
        NonInterfaceConfiguration.class,
        NonInterfaceComponent.class
    );

    private final Set<Class<?>> beansScannedClassesWithoutConfigs = Set.of(
            BasePackageBeansConfig.class,
            PostConstructBeansConfig.class,
            // From BeanConfigBase
            CommonService.class,
            MessageService.class,
            NullService.class,
            // From BeanConfigPostConstruct
            PostConstructService.class,
            DuplicatePostConstructService.class
    );

    private final Set<Class<?>> beansScannedByConfigClass = Set.of(
            BasePackageBeansConfig.class,
            // From BeanConfigBase
            CommonService.class,
            MessageService.class,
            NullService.class
    );

    private final Set<Class<?>> beansScannedByConfigAndOtherClasses = Set.of(
            BasePackageBeansConfig.class,
            CommonService.class,
            MessageService.class,
            NullService.class,
            SomeBean.class,
            PostConstructService.class
    );
    private final Set<Class<?>> beansScannedClasses2 = Set.of(
            PackageScannerBeansConfig.class,
            SomeBean.class,
            SomeBeanSecond.class
    );

    @Test
    void shouldFindAllBeansExceptInterfaces() {
        Set<Class<?>> result = packageScanner.findAllBeanCandidatesByBasePackage("com.bobocode.svydovets.source.interfaces");

        assertThat(result.size()).isEqualTo(beansScannedClassesWithoutInterfaces.size());
        assertThat(result).isEqualTo(beansScannedClassesWithoutInterfaces);
    }

    @Test
    void testScanAllBeansByConfigClass() {
        Set<Class<?>> result = packageScanner.findAllBeanCandidatesByClassTypes(
                BasePackageBeansConfig.class,
                PostConstructBeansConfig.class
        );

        assertThat(result.size()).isEqualTo(beansScannedClassesWithoutConfigs.size());
        assertThat(result).isEqualTo(beansScannedClassesWithoutConfigs);
    }

    @Test
    void testFindAllBeanByBaseClass() {
        Set<Class<?>> result = packageScanner.findAllBeanCandidatesByClassTypes(BasePackageBeansConfig.class);

        assertThat(result.size()).isEqualTo(beansScannedByConfigClass.size());
        assertThat(result).isEqualTo(beansScannedByConfigClass);
    }

    @Test
    void testFindAllBeanByBaseClassOuterPackage() {
        Set<Class<?>> result = packageScanner.findAllBeanCandidatesByClassTypes(PackageScannerBeansConfig.class);

        assertThat(result.size()).isEqualTo(beansScannedClasses2.size());
        assertThat(result).isEqualTo(beansScannedClasses2);
    }

    @Test
    void testFindAllBeanByBaseClassShouldReturnEmptySet() {
        Set<Class<?>> result = packageScanner.findAllBeanCandidatesByClassTypes(NonAnnotatedClass.class);

        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void shouldFindAllBeanFromBeanConfigBaseClassWithAdditionalSpecifiedComponents() {
        Set<Class<?>> result = packageScanner.findAllBeanCandidatesByClassTypes(
                BasePackageBeansConfig.class,
                SomeBean.class,
                PostConstructService.class,
                NonAnnotatedClassFirst.class
        );

        assertThat(result).isEqualTo(beansScannedByConfigAndOtherClasses);
        assertThat(result.contains(NonAnnotatedClassFirst.class)).isFalse();
        assertThat(result.contains(PostConstructService.class)).isTrue();
    }
}
