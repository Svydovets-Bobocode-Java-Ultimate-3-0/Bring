package svydovets.util;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svydovets.core.annotation.Bean;
import svydovets.core.annotation.Component;
import svydovets.core.annotation.Configuration;
import svydovets.web.annotation.PathVariable;
import svydovets.web.annotation.RequestParam;
import svydovets.web.annotation.RestController;

/**
 * Class helper for resolving name of beans, request attributes and parameters
 * Main goal - work with values of annotations for fields, classes or methods
 * It gets values from annotations, convert into name of field/variable
 *
 * @author Renat Safarov, Alex Navozenko, Alex Laskovskyi
 */
public class NameResolver {

    private static final Logger log = LoggerFactory.getLogger(NameResolver.class);

    /**
     * This method helps to define name of declared <strong>bean class</strong> by values from annotation
     *
     * @param beanClass class annotated as bean
     * @return name values from annotation or
     * if value is empty - return default simple name of class with first lower case letter
     * @see Component
     * @see Configuration
     * @see RestController
     */
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

    /**
     * This method helps to define name of declared <strong>bean method</strong> by values from annotation
     *
     * @param initMethod method annotated as bean
     * @return name values from annotation or if value is empty - return method name
     * @see Bean
     */
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

    /**
     * This method helps to define request field name of declared parameter
     *
     * @param parameter set by customer field in request
     * @return name of parameter
     * @see PathVariable
     * @see RequestParam
     */
    public static String resolveRequestParameterName(Parameter parameter) {
        if (parameter.isAnnotationPresent(PathVariable.class)) {
            var parameterName = parameter.getAnnotation(PathVariable.class).value();
            if (!parameterName.isEmpty()) {
                return parameterName;
            }
        }

        if (parameter.isAnnotationPresent(RequestParam.class)) {
            var parameterName = parameter.getAnnotation(RequestParam.class).value();
            if (!parameterName.isEmpty()) {
                return parameterName;
            }
        }
        return parameter.getName();
    }

    private static String setFirstCharacterToLowercase(String beanName) {
        return beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
    }
}
