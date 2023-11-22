package svydovets.web;

import svydovets.web.annotation.PathVariable;
import svydovets.web.dto.RequestInfoHolder;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

public class WebInvocationHandler {

    public Object[] invoke(Class<?> controllerType, Map<String, Object> pathVariableMap, RequestInfoHolder requestInfoHolder) {
        try {
            Method method = controllerType.getDeclaredMethod(requestInfoHolder.getMethodName(), requestInfoHolder.getParameterTypes());

            Parameter[] parameters = method.getParameters();
            Object[] args = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
                if (pathVariable != null) {
                    args[i] = pathVariableMap.get("arg" + i);
                }
            }
            return args;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
