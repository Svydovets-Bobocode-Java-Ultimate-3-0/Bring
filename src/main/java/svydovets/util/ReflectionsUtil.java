package svydovets.util;

import svydovets.core.annotation.Bean;
import svydovets.core.context.beanDefenition.BeanDefinition;
import svydovets.core.context.beanDefenition.DefaultBeanDefinition;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import svydovets.core.annotation.Component;
import svydovets.core.annotation.ComponentScan;
import svydovets.core.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReflectionsUtil {

    public static Set<Class<?>> findAllBeansByBasePackage(final String basePackage) {
        Reflections reflections = new Reflections(basePackage, Scanners.TypesAnnotated);
        var beans = new HashSet<>(reflections.getTypesAnnotatedWith(Component.class));
        //ComponentScan ONLY with Configuration?
        beans.addAll(reflections.getTypesAnnotatedWith(Configuration.class));
        return beans;
    }

    //about @Configuration class mostly
    public static Set<Class<?>> findAllBeanByBaseClass(Class<?> classType) {
        /*Reflections reflections = new Reflections(classType, Scanners.TypesAnnotated);

        return reflections.getTypesAnnotatedWith(Component.class);//???
        */

        Set<Class<?>> beans = new HashSet<>();
        if (classType.isAnnotationPresent(Component.class)) {
            return Set.of(classType);
        } else if (classType.isAnnotationPresent(Configuration.class)) {
            if(classType.isAnnotationPresent(ComponentScan.class)) {
                //beans = gogogo
            }
            Method[] declaredMethods = classType.getDeclaredMethods();
            for (Method method :
                declaredMethods) {
                if (method.isAnnotationPresent(Bean.class)) {
                    //work with this method
                }
            }
        }
        return beans;
    }

    public static Set<Class<?>> findAllBeansFromComponentScan(Class<?>... classTypes) {
        return Stream.of(classTypes)
                .filter(ReflectionsUtil::isComponentScanPresent)
                .map(classType -> classType.getAnnotation(ComponentScan.class).value())
                .flatMap(basePackage -> findAllBeansByBasePackage(basePackage).stream())
                .collect(Collectors.toSet());
    }

    private static boolean isComponentScanPresent(Class<?> classType) {
        return classType.isAnnotationPresent(Configuration.class)
                && classType.isAnnotationPresent(ComponentScan.class);
    }

    private BeanDefinition createBeanDefinitionByBeanClass(Class<?> classType) {
        var beanDefinitionBuilder = DefaultBeanDefinition.builder().beanClass(classType);

        return beanDefinitionBuilder.build();
    }
}
