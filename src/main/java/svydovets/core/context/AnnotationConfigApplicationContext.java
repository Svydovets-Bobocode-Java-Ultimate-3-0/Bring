package svydovets.core.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svydovets.core.context.beanFactory.BeanFactory;

import java.util.Map;

public class AnnotationConfigApplicationContext implements ApplicationContext {

    private static final Logger log = LoggerFactory.getLogger(AnnotationConfigApplicationContext.class);

    protected final BeanFactory beanFactory = new BeanFactory();

    public AnnotationConfigApplicationContext(String basePackage) {
        log.info("Start creating an application context");
        beanFactory.registerBeans(basePackage);
        log.info("Finish creating an application context");
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
