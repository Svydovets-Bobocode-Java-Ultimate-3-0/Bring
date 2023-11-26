package svydovets.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svydovets.core.annotation.Autowired;
import svydovets.exception.NoDefaultConstructor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReflectionsUtil {

    private static final Logger log = LoggerFactory.getLogger(ReflectionsUtil.class);

    public static List<String> findAutowiredFieldNames(Class<?> beanClass) {
        log.trace("Call findAutowiredFieldNames({})", beanClass);
        var fields = beanClass.getDeclaredFields();

        List<String> autowireFieldNames = Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(Autowired.class))
                .map(Field::getName)
                .toList();
        log.trace("Founded autowire field names: {}", autowireFieldNames);

        return autowireFieldNames;
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
