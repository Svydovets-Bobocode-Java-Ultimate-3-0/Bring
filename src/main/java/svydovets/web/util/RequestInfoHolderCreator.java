package svydovets.web.util;

import svydovets.web.annotation.PathVariable;
import svydovets.web.annotation.RequestParam;
import svydovets.web.dto.RequestInfoHolder;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * Utility class responsible for creating {@link RequestInfoHolder} instances based on
 * the information extracted from a method and its associated annotations.
 */
public class RequestInfoHolderCreator {

    private RequestInfoHolderCreator() {
    }

    /**
     * Creates a new {@link RequestInfoHolder} instance using the provided entry name and method.
     *
     * @param className  Class name of bean entry.
     * @param method The method for which to create the request information holder.
     * @return       A new RequestInfoHolder instance populated with relevant information.
     */
    public static RequestInfoHolder create(String className, Class<?> classType, Method method) {
        RequestInfoHolder requestInfoHolder = new RequestInfoHolder(className, classType);
        requestInfoHolder.setMethodName(method.getName());
        requestInfoHolder.setParameterTypes(method.getParameterTypes());
        String[] parameterNames = getParameterNames(method);
        requestInfoHolder.setParameterNames(parameterNames);

        return requestInfoHolder;
    }

    private static String[] getParameterNames(Method method) {
        return Arrays.stream(method.getParameters())
                .map(RequestInfoHolderCreator::defineName).toArray(String[]::new);
    }

    private static String defineName(Parameter parameter) {
        if(parameter.isAnnotationPresent(PathVariable.class)) {
            return parameter.getDeclaredAnnotation(PathVariable.class).value();
        }

        if(parameter.isAnnotationPresent(RequestParam.class)) {
            return parameter.getDeclaredAnnotation(RequestParam.class).value();
        }

        return parameter.getName();
    }

}
