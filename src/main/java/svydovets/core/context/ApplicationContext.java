package svydovets.core.context;

import java.util.Map;

public interface ApplicationContext {
    <T> T getBean(Class<T> requiredType);

    <T> T getBean(String name, Class<T> requiredType);

    <T> Map<String, T> getBeansOfType(Class<T> requiredType);
}
