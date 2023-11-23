package svydovets.core.context;

import svydovets.core.context.beanFactory.BeanFactory;

import java.util.Map;

public class AnnotationConfigApplicationContext implements ApplicationContext {
    protected final BeanFactory beanFactory = new BeanFactory();

    public AnnotationConfigApplicationContext(String basePackage) {
        beanFactory.registerBeans(basePackage);
    }

    public AnnotationConfigApplicationContext(Class<?>... classes) { // @Config or @Component
        beanFactory.registerBeans(classes);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        return beanFactory.getBean(requiredType);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        return beanFactory.getBean(name, requiredType);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> requiredType) {
        return beanFactory.getBeansOfType(requiredType);
    }

    @Override
    public Map<String, Object> getBeans() {
        return beanFactory.getBeans();
    }
}
