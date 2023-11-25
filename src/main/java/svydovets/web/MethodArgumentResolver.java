package svydovets.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import svydovets.exception.UnsupportedTypeException;
import svydovets.web.annotation.PathVariable;
import svydovets.web.annotation.RequestBody;
import svydovets.web.annotation.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;

public class MethodArgumentResolver {

    public Object[] invoke(Method method, ServletWebRequest servletWebRequest) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> parameterType = parameter.getType();

            String parameterName = parameter.getName();

            if (parameter.isAnnotationPresent(PathVariable.class)) {
                String parameterValue = servletWebRequest.getPathVariableValue(parameterName);

                args[i] = convertRequestParameterValue(parameterType, parameterValue);
            } else if (parameter.isAnnotationPresent(RequestParam.class)) {
                String parameterValue = servletWebRequest.getRequestParameterValue(parameterName);

                args[i] = convertRequestParameterValue(parameterType, parameterValue);
            } else if (parameter.isAnnotationPresent(RequestBody.class)) {
                args[i] = parameterType.cast(servletWebRequest.getRequestBody(parameterType));
            } else if (HttpServletRequest.class == parameterType) {
                args[i] = servletWebRequest.getRequest();
            } else if (HttpServletResponse.class == parameterType) {
                args[i] = servletWebRequest.getResponse();
            }
        }
        return args;
    }


    public Object convertRequestParameterValue(Class<?> parameterType, String requestParameterValue) {
        if (isNumeric(parameterType)) {
            return convertNumber(parameterType, requestParameterValue);
        } else if (parameterType == String.class) {
            return requestParameterValue;
        } else if (parameterType == boolean.class) {
            return Boolean.valueOf(requestParameterValue);
        } else if (parameterType == char.class) {
            return requestParameterValue.charAt(0);
        } else {
            // todo: LOG ERROR
            throw new UnsupportedTypeException(String.format(
                    "Unsupported parameter type: %s",
                    parameterType)
            );
        }
    }

    private boolean isNumeric(Class<?> parameterType) {
        return Number.class.isAssignableFrom(parameterType) || isPrimitiveNumeric(parameterType);
    }

    private boolean isPrimitiveNumeric(Class<?> parameterType) {
        return parameterType == long.class || parameterType == int.class || parameterType == float.class || parameterType == double.class;
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
        } else if (parameterType == BigDecimal.class) {
            return BigDecimal.valueOf(Long.parseLong(requestParameterValue));
        } else {
            // todo: LOG ERROR
            throw new UnsupportedTypeException(String.format(
                    "Unsupported number type: %s",
                    parameterType)
            );
        }
    }
}
