package svydovets.util;

import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import svydovets.core.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.reflections.scanners.Scanners.SubTypes;

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

    /**
     * Returns interfaces that class implemented.
     * <p>
     * For example,
     * <pre>
     * class ServiceImpl implements Service, Serializable {}
     * </pre>
     * <p>
     * The method returns the set that includes Service and Serializable interfaces.
     *
     * @param classType class is represented bean
     * @return {@code Set<Class<?>>}
     */
    public static Set<Class<?>> findImplementedInterfacesOfClass(Class<?> classType) {
        Reflections reflections = new Reflections(classType, Scanners.TypesAnnotated);
        return reflections.get(ReflectionUtils.Interfaces.of(classType));
    }

    /**
     * Returns classes that are implemented the interface.
     * <p>
     * For example,
     * <pre>
     * interface Service {}
     * class FirstServiceImpl implements Service {}
     * class SecondServiceImpl implements Service {}
     * </pre>
     * <p>
     * The method returns the set that includes FirstServiceImpl and SecondServiceImpl classes.
     *
     * @param interfaceType interface that is used for finding classes that are implemented this interface
     * @return {@code Set<Class<?>>} set of classes
     */
    public static Set<Class<?>> findClassesImplementedInterface(Class<?> interfaceType) {
        Reflections reflections = new Reflections(interfaceType, SubTypes);
        return reflections.get(SubTypes.of(interfaceType).asClass());
    }

    private static boolean isComponentScanPresent(Class<?> classType) {
        return classType.isAnnotationPresent(Configuration.class)
                && classType.isAnnotationPresent(ComponentScan.class);
    }
}
