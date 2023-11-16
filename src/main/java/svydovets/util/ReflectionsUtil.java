package svydovets.util;

import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.ComponentScan;
import svydovets.core.annotation.Configuration;
import svydovets.exception.NoDefaultConstructor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.reflections.scanners.Scanners.SubTypes;

public class ReflectionsUtil {

    public static List<String> findAutowiredFieldNames(Class<?> beanClass) {
        var fields = beanClass.getDeclaredFields();

        return Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(Autowired.class))
                .map(Field::getName)
                .collect(Collectors.toList());
    }

    public static Constructor<?> getPreparedNoArgsConstructor(Class<?> beanType) {
        try {
            Constructor<?> constructor = beanType.getDeclaredConstructor();
            return prepareConstructor(constructor);
        } catch (NoSuchMethodException e) {
            throw new NoDefaultConstructor(String.format("No default constructor found of type %s", beanType.getName()));
        }
    }

    public static Constructor<?> prepareConstructor(Constructor<?> constructor) {
        constructor.setAccessible(true);
        return constructor;
    }

    public static Method prepareMethod(Method method) {
        method.setAccessible(true);
        return method;
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
