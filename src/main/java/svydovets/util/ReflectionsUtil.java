package svydovets.util;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import svydovets.core.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReflectionsUtil {

    public static Set<Class<?>> findAllBeanByBasePackage(final String basePackage) {
        Reflections reflections = new Reflections(basePackage, Scanners.TypesAnnotated);

        return reflections.getTypesAnnotatedWith(Component.class);
    }

    public static Set<Class<?>> findAllBeanByBaseClass(Class<?> classType) {
        Reflections reflections = new Reflections(classType, Scanners.TypesAnnotated);

        return reflections.getTypesAnnotatedWith(Component.class);
    }

    public static Set<Class<?>> findAllBeanFromComponentScan(Class<?>... classTypes) {
        return Stream.of(classTypes)
                .filter(ReflectionsUtil::isComponentScanPresent)
                .map(classType -> classType.getAnnotation(ComponentScan.class).value())
                .flatMap(basePackage -> findAllBeanByBasePackage(basePackage).stream())
                .collect(Collectors.toSet());
    }

    public static List<String> findAutowiredFieldNames(Class<?> beanClass) {
        var fields = beanClass.getDeclaredFields();

        return Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(Autowired.class))
                .map(field -> {
                    if(field.isAnnotationPresent(Qualifier.class)) {
                        Qualifier qualifier = field.getAnnotation(Qualifier.class);
                        return qualifier.value();
                    }

                    return field.getName();
                })
                .collect(Collectors.toList());
    }

    private static boolean isComponentScanPresent(Class<?> classType) {
        return classType.isAnnotationPresent(Configuration.class)
                && classType.isAnnotationPresent(ComponentScan.class);
    }
}
