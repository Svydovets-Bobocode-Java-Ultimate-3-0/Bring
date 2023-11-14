package svydovets.util;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import svydovets.core.annotation.Component;
import svydovets.core.annotation.ComponentScan;
import svydovets.core.annotation.Configuration;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PackageScanner {


    // todo:
    public Set<Class<?>> findAllBeanByBasePackage(String basePackage) {
        Reflections reflections = new Reflections(basePackage, Scanners.TypesAnnotated);
        Set<Class<?>> beanClasses = findComponentByBasePackage(basePackage);
        Set<Class<?>> configClasses = reflections.getTypesAnnotatedWith(Configuration.class);
        if (Objects.nonNull(configClasses)) {
            beanClasses.addAll(configClasses);
            scanAllBeansByConfigClass(configClasses, beanClasses);
        }
        return beanClasses;
    }

    public void scanAllBeansByConfigClass(Set<Class<?>> configClasses, Set<Class<?>> beanClasses) {
        Set<Class<?>> configIsScanPresent = configClasses.stream().filter(this::isComponentScanPresent)
                .collect(Collectors.toSet());
        if (!configIsScanPresent.isEmpty()) {
            Set<Class<?>> classesFromComponentScan = new HashSet<>();
            configClasses.forEach(config -> {
                classesFromComponentScan.addAll(findAllBeanByBaseClass(config));
            });
            beanClasses.addAll(classesFromComponentScan);
        }
    }

    private Set<Class<?>> findComponentByBasePackage(String basePackage) {
        Reflections reflections = new Reflections(basePackage, Scanners.TypesAnnotated);
        return reflections.getTypesAnnotatedWith(Component.class);
    }

    public Set<Class<?>> findAllBeanByBaseClass(Class<?> classType) {
//        Reflections reflections = new Reflections(classType, Scanners.TypesAnnotated);
        return Optional.of(classType)
                .filter(this::isComponentScanPresent)
                .map(clazz -> clazz.getAnnotation(ComponentScan.class).value())
                .map(this::findComponentByBasePackage)
                .orElseGet(HashSet::new);
//        return reflections.getTypesAnnotatedWith(Component.class);
    }

    private boolean isComponentScanPresent(Class<?> classType) {
        return classType.isAnnotationPresent(ComponentScan.class);
    }
}
