package svydovets.util;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svydovets.core.annotation.Component;
import svydovets.core.annotation.ComponentScan;
import svydovets.core.annotation.Configuration;
import svydovets.web.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

public class PackageScanner {

    private static final Logger log = LoggerFactory.getLogger(PackageScanner.class);

    public Set<Class<?>> findComponentsByBasePackage(String basePackage) {
        log.trace("Call findComponentsByBasePackage({})", basePackage);
        Reflections reflections = new Reflections((Object) basePackage);
        Set<Class<?>> beanClasses = reflections.getTypesAnnotatedWith(Component.class);
        beanClasses.addAll(reflections.getTypesAnnotatedWith(Configuration.class));
        beanClasses.addAll(reflections.getTypesAnnotatedWith(RestController.class));
        beanClasses.removeIf(Class::isInterface);
        return beanClasses;
    }

    public Set<Class<?>> findAllBeanCandidatesByBaseClass(Class<?>... classTypes) {
        log.trace("Call findAllBeanCandidatesByBaseClass({})", (Object[]) classTypes);
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

        log.trace("Found set of bean classes: {}", beanClasses);

        return beanClasses;
    }
}
