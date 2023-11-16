package svydovets.util;

import org.reflections.Reflections;
import svydovets.core.annotation.Component;
import svydovets.core.annotation.ComponentScan;
import svydovets.core.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

public class PackageScanner {

    public Set<Class<?>> findComponentsByBasePackage(String basePackage) {
        Reflections reflections = new Reflections((Object) basePackage);
        return reflections.getTypesAnnotatedWith(Component.class);
    }

    public Set<Class<?>> findAllBeanCandidatesByBaseClass(Class<?>... classTypes) {
        Set<Class<?>> beanClasses = new HashSet<>();

        for (Class<?> beanClass : classTypes) {
            if (beanClass.isAnnotationPresent(Configuration.class) || beanClass.isAnnotationPresent(Component.class)) {
                beanClasses.add(beanClass);
            }
            ComponentScan componentScan = beanClass.getAnnotation(ComponentScan.class);
            if (componentScan != null) {
                beanClasses.addAll(findComponentsByBasePackage(componentScan.value()));
            }
        }

        return beanClasses;
    }
}
