package svydovets.web;

import static svydovets.util.NameResolver.resolveRequestParameterName;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import svydovets.exception.UnsupportedTypeException;
import svydovets.web.annotation.PathVariable;
import svydovets.web.annotation.RequestBody;
import svydovets.web.annotation.RequestParam;
import svydovets.web.path.RequestInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.Map;

public class WebInvocationHandler {

    public Object[] invoke(Method method, RequestInfo requestInfo) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> parameterType = parameter.getType();

            if (parameter.isAnnotationPresent(PathVariable.class)) {
                String parameterName = resolveRequestParameterName(parameter);
                Map<String, String> pathVariableValuesMap = requestInfo.pathVariableValuesMap();
                String parameterValue = pathVariableValuesMap.get(parameterName);

                args[i] = convertRequestParameterValue(parameterType, parameterValue);
            } else if (parameter.isAnnotationPresent(RequestParam.class)) {
                String parameterName = resolveRequestParameterName(parameter);
                Map<String, String[]> requestParameterValuesMap = requestInfo.requestParameterValuesMap();
                String parameterValue = getRequestParameterValue(parameterName, requestParameterValuesMap);

                args[i] = convertRequestParameterValue(parameterType, parameterValue);
            } else if (parameter.isAnnotationPresent(RequestBody.class)) {
                args[i] = parameterType.cast(parseRequestBodyJson(requestInfo.requestBody(), parameterType));
            } 
        }
        return args;
    }

    private Object parseRequestBodyJson(String requestBody, Class<?> parameterType) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(requestBody, parameterType);
        } catch (JsonProcessingException e) {
            // todo: LOG ERROR
            throw new RuntimeException("Error processing JSON request body", e);
        }
    }

    private String getRequestParameterValue(String parameterName, Map<String, String[]> requestParameterValuesMap) {
        String[] requestParameters = requestParameterValuesMap.get(parameterName);
        return requestParameters == null
                ? null
                : requestParameters[0];
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
