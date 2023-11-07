package svydovets.core.context;

import java.util.Map;

public class DefaultApplicationContext implements ApplicationContext {

    @Override
    public <T> T getBean(Class<T> requiredType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> requiredType) {
        throw new UnsupportedOperationException();
    }
}
