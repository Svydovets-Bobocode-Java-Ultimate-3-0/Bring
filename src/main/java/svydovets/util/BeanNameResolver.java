package svydovets.util;

import java.lang.reflect.Method;

import svydovets.core.annotation.Bean;
import svydovets.core.annotation.Component;
import svydovets.core.annotation.Configuration;

public class BeanNameResolver {

    //create javadoc for description
    public static String resolveBeanName(Class<?> beanClass) {
        if (beanClass.isAnnotationPresent(Configuration.class)) {
            var beanValue = beanClass.getAnnotation(Configuration.class).value();
            if (!beanValue.isEmpty()) {
                return beanValue;
            }
        }
        if (beanClass.isAnnotationPresent(Component.class)) {
            var beanValue = beanClass.getAnnotation(Component.class).value();
            if (!beanValue.isEmpty()) {
                return beanValue;
            }
        }
        return setFirstCharacterToLowercase(beanClass.getSimpleName());
    }

    //create javadoc for description
    public static String resolveBeanName(Method initMethod) {
        if (initMethod.isAnnotationPresent(Bean.class)) {
            var beanValue = initMethod.getAnnotation(Bean.class).value();
            return beanValue.isEmpty() ?
                initMethod.getName() :
                beanValue;
        }
        //throw exception if not bean annotated
        // todo: Add test to this case
        throw new RuntimeException();
    }

    private static String setFirstCharacterToLowercase(String beanName) {
        return beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
    }
}
