package svydovets.core.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svydovets.core.context.beanFactory.BeanFactoryImpl;

import java.util.Map;

/**
 * The {@code AnnotationConfigApplicationContext} class is an implementation of the {@link ApplicationContext}
 * interface. It provides a simplified and annotation-driven approach to configuring and managing beans in an application context.
 *
 * <p>This context supports two main ways of defining and registering beans:
 * <ul>
 *     <li>Scanning a specified base package for classes annotated with {@code @Config} or {@code @Component}.</li>
 *     <li>Registering specific classes directly through the constructor</li>
 * </ul>
 *
 * <p>The lifecycle of this application context involves the creation and initialization of a {@link svydovets.core.context.beanFactory.BeanFactory},
 * which is responsible for registering and managing beans. Beans can be retrieved from the context based on their type,
 * name, or all beans of a specific type.
 *
 * <p>Usage of this context involves creating an instance with either a base package for component scanning or specific
 * annotated classes. The context then scans or registers beans during its initialization process.
 *
 * <p>This class is intended to serve as a basic application context, providing a foundation for
 * dependency injection and bean management. It simplifies the configuration of beans by relying on annotations to
 * identify and register beans in the context.
 *
 *
 * @author Your Name
 * @version 1.0
 * @see ApplicationContext
 * @see svydovets.core.context.beanFactory.BeanFactory
 */
public class AnnotationConfigApplicationContext implements ApplicationContext {

    private static final Logger log = LoggerFactory.getLogger(AnnotationConfigApplicationContext.class);

    /**
     * The underlying {@link BeanFactoryImpl} responsible for registering and managing beans.
     */
    protected final BeanFactoryImpl beanFactoryImpl = new BeanFactoryImpl();

    /**
     * Creates an {@code AnnotationConfigApplicationContext} by scanning the specified base package for
     * classes annotated with {@code @Config} or {@code @Component}.
     *
     * @param basePackage the base package to scan for annotated classes
     */
    public AnnotationConfigApplicationContext(String basePackage) {
        log.info("Start creating an application context");
        beanFactoryImpl.registerBeans(basePackage);
        log.info("Finish creating an application context");
    }

    /**
     * Creates an {@code AnnotationConfigApplicationContext} by directly registering specified classes,
     * where the classes are annotated with {@code @Config} or {@code @Component}.
     *
     * @param classes the annotated classes to register in the context
     */
    public AnnotationConfigApplicationContext(Class<?>... classes) {
        beanFactoryImpl.registerBeans(classes);
    }

    /**
     * <p>Retrieves a bean of the specified type from the context.
     *
     * @param requiredType the type of the bean to retrieve
     * @param <T>          the generic type of the bean
     * @return the bean instance
     */
    @Override
    public <T> T getBean(Class<T> requiredType) {
        return beanFactoryImpl.getBean(requiredType);
    }

    /**
     * <p>Retrieves a bean of the specified type with the given name from the context.
     *
     * @param name         the name of the bean to retrieve
     * @param requiredType the type of the bean to retrieve
     * @param <T>          the generic type of the bean
     * @return the bean instance
     */
    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        return beanFactoryImpl.getBean(name, requiredType);
    }

    /**
     * <p>Retrieves all beans of the specified type from the context.
     *
     * @param requiredType the type of the beans to retrieve
     * @param <T>          the generic type of the beans
     * @return a map of bean names to bean instances
     */
    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> requiredType) {
        return beanFactoryImpl.getBeansOfType(requiredType);
    }

    /**
     * <p>Retrieves all beans from the context.
     *
     * @return a map of bean names to bean instances
     */
    @Override
    public Map<String, Object> getBeans() {
        return beanFactoryImpl.getBeans();
    }
}

