package svydovets.util;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import svydovets.core.annotation.Component;
import svydovets.core.annotation.ComponentScan;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Copy of RelfectionsUtil class
 */
public class PackageScanner {

    // todo: Implement BR-10
    public Set<Class<?>> findAllBeanByBasePackage(String basePackage) {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackages(basePackage)
                        .setScanners(Scanners.TypesAnnotated)
        );
//        Set<Class<?>> beanClasses = findComponentByBasePackage(basePackage);
//        Set<Class<?>> configClasses = reflections.getTypesAnnotatedWith(Configuration.class);
//        if (Objects.nonNull(configClasses)) {
//            beanClasses.addAll(configClasses);
//        }
//        Set<Class<?>> configIsScanPresent = configClasses.stream()
//                .filter(this::isComponentScanPresent)
//                .collect(Collectors.toSet());
//        if (!configIsScanPresent.isEmpty()) {
//            Set<Class<?>> classesFromComponentScan = new HashSet<>();
//            configClasses.forEach(config -> {
//                classesFromComponentScan.addAll(findAllBeanByBaseClass(config));
//            });
//            beanClasses.addAll(classesFromComponentScan);
//        }
        return reflections.getTypesAnnotatedWith(Component.class);
    }

    private Set<Class<?>> findComponentByBasePackage(String basePackage) {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackages(basePackage)
                        .setScanners(Scanners.TypesAnnotated)
        );
        return reflections.getTypesAnnotatedWith(Component.class);
    }

    public Set<Class<?>> findAllBeanByBaseClass(Class<?>... classType) {
//        return Optional.of(classType)
//                .filter(this::isComponentScanPresent)
//                .map(clazz -> clazz.getAnnotation(ComponentScan.class).value())
//                .map(this::findComponentByBasePackage)
//                .orElseGet(HashSet::new);
        return Set.of();
    }

    private boolean isComponentScanPresent(Class<?> classType) {
        return classType.isAnnotationPresent(ComponentScan.class);
    }
}
