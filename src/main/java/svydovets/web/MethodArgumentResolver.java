package svydovets.web;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import svydovets.exception.UnsupportedTypeException;
import svydovets.util.ErrorMessageConstants;
import svydovets.web.annotation.PathVariable;
import svydovets.web.annotation.RequestBody;
import svydovets.web.annotation.RequestParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MethodArgumentResolver {

    private static final Set<Class<? extends Annotation>> SUPPORTED_ANNOTATIONS = new HashSet<>(Arrays.asList(
            PathVariable.class,
            RequestParam.class,
            RequestBody.class
    ));

    private static final Set<Class<?>> SUPPORTED_TYPES = new HashSet<>(Arrays.asList(
            String.class,
            long.class, Long.class,
            int.class, Integer.class,
            float.class, Float.class,
            double.class, Double.class,
            boolean.class, Boolean.class,
            char.class, Character.class
    ));

    public Object[] resolveArguments(Method method, ServletWebRequest servletWebRequest) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            args[i] = resolveArgument(parameter, servletWebRequest);
        }
        return args;
    }

    private Object resolveArgument(Parameter parameter, ServletWebRequest servletWebRequest) {
        for (Class<? extends Annotation> annotationType : SUPPORTED_ANNOTATIONS) {
            if (parameter.isAnnotationPresent(annotationType)) {
                return handleAnnotation(parameter, annotationType, servletWebRequest);
            }
        }

        Class<?> parameterType = parameter.getType();
        if (ServletRequest.class.isAssignableFrom(parameterType)) {
            return servletWebRequest.getRequest();
        } else if (ServletResponse.class.isAssignableFrom(parameterType)) {
            return servletWebRequest.getResponse();
        }

        // todo: LOG ERROR
        throw new UnsupportedTypeException(String.format(
                ErrorMessageConstants.UNSUPPORTED_TYPE_ERROR_MESSAGE,
                parameterType)
        );
    }

    private Object handleAnnotation(Parameter parameter, Class<? extends Annotation> annotationType, ServletWebRequest servletWebRequest) {
        Class<?> parameterType = parameter.getType();
        String parameterName = parameter.getName();
        if (annotationType == PathVariable.class) {
            String parameterValue = servletWebRequest.getPathVariableValue(parameterName);
            return convertRequestParameterValue(parameterType, parameterValue);
        } else if (annotationType == RequestParam.class) {
            String parameterValue = servletWebRequest.getRequestParameterValue(parameterName);
            return convertRequestParameterValue(parameterType, parameterValue);
        } else {
            return parameterType.cast(servletWebRequest.getRequestBody(parameterType));
        }
    }

    public Object convertRequestParameterValue(Class<?> parameterType, String requestParameterValue) {
        if (SUPPORTED_TYPES.contains(parameterType)) {
            if (isNumeric(parameterType)) {
                return convertNumber(parameterType, requestParameterValue);
            } else if (parameterType == String.class) {
                return requestParameterValue;
            } else if (parameterType == boolean.class) {
                return Boolean.valueOf(requestParameterValue);
            } else if (parameterType == char.class) {
                return requestParameterValue.charAt(0);
            }
        }
        // todo: LOG ERROR
        throw new UnsupportedTypeException(String.format(
                ErrorMessageConstants.UNSUPPORTED_TYPE_ERROR_MESSAGE,
                parameterType)
        );
    }

    private boolean isNumeric(Class<?> parameterType) {
        return Number.class.isAssignableFrom(parameterType) || isPrimitiveNumeric(parameterType);
    }

    private boolean isPrimitiveNumeric(Class<?> parameterType) {
        return parameterType == long.class
                || parameterType == int.class
                || parameterType == float.class
                || parameterType == double.class;
    }

    private Object convertNumber(Class<?> parameterType, String requestParameterValue) {
        if (parameterType == long.class || parameterType == Long.class) {
            return Long.parseLong(requestParameterValue);
        } else if (parameterType == int.class || parameterType == Integer.class) {
            return Integer.parseInt(requestParameterValue);
        } else if (parameterType == double.class || parameterType == Double.class) {
            return Double.parseDouble(requestParameterValue);
        } else if (parameterType == float.class || parameterType == Float.class) {
            return Float.valueOf(requestParameterValue);
        }
        // todo: LOG ERROR
        throw new UnsupportedTypeException(String.format(
                ErrorMessageConstants.UNSUPPORTED_NUMBER_TYPE_ERROR_MESSAGE,
                parameterType)
        );
    }
}
