package svydovets.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svydovets.core.annotation.Autowired;
import svydovets.util.exception.NoDefaultConstructorException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static svydovets.util.ErrorMessageConstants.NO_DEFAULT_CONSTRUCTOR_FOUND_OF_TYPE;

public class ReflectionsUtil {

    private static final Logger log = LoggerFactory.getLogger(ReflectionsUtil.class);

    public static List<String> findAutowiredFieldNames(Class<?> beanClass) {
        var fields = beanClass.getDeclaredFields();

        List<String> autowireFieldNames = Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(Autowired.class))
                .map(Field::getName)
                .toList();

        return autowireFieldNames;
    }

    public static <T> Constructor<T> getPreparedNoArgsConstructor(Class<T> beanType) {
        try {
            Constructor<T> constructor = beanType.getDeclaredConstructor();

            return prepareConstructor(constructor);
        } catch (NoSuchMethodException exception) {
            String errorMessage = String.format(NO_DEFAULT_CONSTRUCTOR_FOUND_OF_TYPE, beanType.getName());
            log.error(errorMessage);
            throw new NoDefaultConstructorException(errorMessage, exception);
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
