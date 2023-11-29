package svydovets.util;

import org.reflections.Reflections;
import svydovets.core.annotation.Component;
import svydovets.core.annotation.ComponentScan;
import svydovets.core.annotation.Configuration;
import svydovets.web.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

public class PackageScanner {

    public Set<Class<?>> findComponentsByBasePackage(String basePackage) {
        Reflections reflections = new Reflections((Object) basePackage);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Component.class);
        classes.addAll(reflections.getTypesAnnotatedWith(Configuration.class));
        classes.removeIf(Class::isInterface);
        return classes;
    }

    public Set<Class<?>> findComponentsByClass(Class<?> classType) {
        return findComponentsByBasePackage(classType.getPackageName());
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
