package svydovets.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(RequestInfoHolderCreator.class);

    private RequestInfoHolderCreator() {
        //Empty constructor
    }

    /**
     * Creates a new {@link RequestInfoHolder} instance using the provided entry name and method.
     *
     * @param className  Class name of bean entry.
     * @param method The method for which to create the request information holder.
     * @return       A new RequestInfoHolder instance populated with relevant information.
     */
    public static RequestInfoHolder create(String className, Class<?> classType, Method method) {
        log.trace("Call create({}, {}, {})", className, classType, method);

        RequestInfoHolder requestInfoHolder = new RequestInfoHolder(className, classType);
        requestInfoHolder.setMethodName(method.getName());
        requestInfoHolder.setParameterTypes(method.getParameterTypes());
        String[] parameterNames = getParameterNames(method);
        requestInfoHolder.setParameterNames(parameterNames);

        log.trace("Created requestInfoHolder: {}", requestInfoHolder);

        return requestInfoHolder;
    }

    private static String[] getParameterNames(Method method) {
        log.trace("Call getParameterNames({})", method);

        return Arrays.stream(method.getParameters())
                .map(RequestInfoHolderCreator::defineName).toArray(String[]::new);
    }

    private static String defineName(Parameter parameter) {
        log.trace("Call defineName({})", parameter);
        if(parameter.isAnnotationPresent(PathVariable.class)) {
            return parameter.getDeclaredAnnotation(PathVariable.class).value();
        }

        if(parameter.isAnnotationPresent(RequestParam.class)) {
            return parameter.getDeclaredAnnotation(RequestParam.class).value();
        }

        log.trace("return parameterName: {}", parameter.getName());

        return parameter.getName();
    }

}
