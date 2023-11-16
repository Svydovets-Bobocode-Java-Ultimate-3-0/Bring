package svydovets.util;

import svydovets.core.annotation.Autowired;
import svydovets.exception.NoDefaultConstructor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReflectionsUtil {

    public static final String NO_DEFAULT_CONSTRUCTOR_FOUND_OF_TYPE = "No default constructor found of type %s";

    public static List<String> findAutowiredFieldNames(Class<?> beanClass) {
        var fields = beanClass.getDeclaredFields();

        return Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(Autowired.class))
                .map(Field::getName)
                .collect(Collectors.toList());
    }

    public static <T> Constructor<T> getPreparedNoArgsConstructor(Class<T> beanType) {
        try {
            Constructor<T> constructor = beanType.getDeclaredConstructor();

            return prepareConstructor(constructor);
        } catch (NoSuchMethodException exception) {
            throw new NoDefaultConstructor(String.format(NO_DEFAULT_CONSTRUCTOR_FOUND_OF_TYPE, beanType.getName()));
        }
    }

    public static <T> Constructor<T> prepareConstructor(Constructor<T> constructor) {
        constructor.setAccessible(true);

        return constructor;
    }

    public static Method prepareMethod(Method method) {
        method.setAccessible(true);
        return method;
    }
}
