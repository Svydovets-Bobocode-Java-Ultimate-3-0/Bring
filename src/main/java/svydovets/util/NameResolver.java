package svydovets.util;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import svydovets.core.annotation.Bean;
import svydovets.core.annotation.Component;
import svydovets.core.annotation.Configuration;
import svydovets.exception.NoSuchBeanException;
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

        return setFirstCharacterToLowercase(beanClass.getSimpleName());
    }

    /**
     * This method helps to define name of declared <strong>bean method</strong> by values from annotation
     *
     * @param initMethod method annotated as bean
     * @return name values from annotation or if value is empty - return method name
     * @see Bean
     */
    public static String resolveBeanName(Method initMethod) {
        String methodName = initMethod.getName();
        if (initMethod.isAnnotationPresent(Bean.class)) {
            var beanValue = initMethod.getAnnotation(Bean.class).value();
            return beanValue.isEmpty() ?
                methodName :
                beanValue;
        }
        throw new NoSuchBeanException(String.format("Method %s is not defined as a bean method", methodName));
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
