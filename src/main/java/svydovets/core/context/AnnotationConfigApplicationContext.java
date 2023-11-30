package svydovets.core.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svydovets.core.context.beanFactory.BeanFactoryImpl;

import java.util.Map;

public class AnnotationConfigApplicationContext implements ApplicationContext {

    private static final Logger log = LoggerFactory.getLogger(AnnotationConfigApplicationContext.class);

    protected final BeanFactoryImpl beanFactoryImpl = new BeanFactoryImpl();

    public AnnotationConfigApplicationContext(String basePackage) {
        log.info("Start creating an application context");
        beanFactoryImpl.registerBeans(basePackage);
        log.info("Finish creating an application context");
    }

    public AnnotationConfigApplicationContext(Class<?>... classes) { // @Config or @Component
        beanFactoryImpl.registerBeans(classes);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        return beanFactoryImpl.getBean(requiredType);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        return beanFactoryImpl.getBean(name, requiredType);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> requiredType) {
        return beanFactoryImpl.getBeansOfType(requiredType);
    }

    @Override
    public Map<String, Object> getBeans() {
        return beanFactoryImpl.getBeans();
    }
}
