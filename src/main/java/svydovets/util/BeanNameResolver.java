package svydovets.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svydovets.core.annotation.Bean;
import svydovets.core.annotation.Component;
import svydovets.core.annotation.Configuration;
import svydovets.web.annotation.RestController;

import java.lang.reflect.Method;

public class BeanNameResolver {

    private static final Logger log = LoggerFactory.getLogger(BeanNameResolver.class);

    //create javadoc for description
    public static String resolveBeanName(Class<?> beanClass) {
        log.trace("Call resolveBeanName({}) for class base bean", beanClass);
        if (beanClass.isAnnotationPresent(RestController.class)) {
            var beanValue = beanClass.getAnnotation(RestController.class).value();
            if (!beanValue.isEmpty()) {
                return beanValue;
            }
        }

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

        String beanName = setFirstCharacterToLowercase(beanClass.getSimpleName());
        log.trace("Bean name is not specified explicitly, simple class name has been used {}", beanName);

        return beanName;
    }

    //create javadoc for description
    public static String resolveBeanName(Method initMethod) {
        log.trace("Call resolveBeanName({}) for method base bean", initMethod);
        var beanName= initMethod.getAnnotation(Bean.class).value();
        if (beanName.isEmpty()) {
            log.trace("Bean name is not specified explicitly, method name has been used {}", initMethod);

            return initMethod.getName();
        } else {
            log.trace("Bean name is specified explicitly '{}'", beanName);

            return beanName;
        }
    }

    private static String setFirstCharacterToLowercase(String beanName) {
        return beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
    }
}
