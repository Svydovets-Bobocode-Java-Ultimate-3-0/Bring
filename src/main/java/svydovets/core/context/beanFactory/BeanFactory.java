package svydovets.core.context.beanFactory;

import svydovets.core.context.beanDefinition.BeanDefinition;

import java.util.Map;

/**
 * The {@code BeanFactory} interface is a central component of our custom Inversion of Control (IoC) framework,
 * responsible for managing the lifecycle, configuration, and retrieval of beans within an application.
 * This interface follows the IoC design pattern, where the control over the object lifecycle is inverted from
 * the application code to the framework, facilitating loose coupling and improved maintainability.
 * <p>
 * This interface provides methods for registering and retrieving beans, offering developers flexibility in
 * configuring and customizing their application components.
 *
 * <h2>Bean Registration</h2>
 * The following methods are available for registering beans:
 * <ul>
 *     <li>{@link #registerBeans(String) registerBeans(String basePackage)}: Scans the specified base package for
 *     classes annotated as beans and registers them in the IoC container.</li>
 *     <li>{@link #registerBeans(Class[]) registerBeans(Class<?>... classes)}: Manually registers the provided classes
 *     as beans in the IoC container.</li>
 *     <li>{@link #registerBean(String, BeanDefinition) registerBean(String beanName, BeanDefinition beanDefinition)}:
 *     Manually registers a bean with a specified name and its corresponding {@link BeanDefinition}.</li>
 * </ul>
 *
 * <h2>Bean Retrieval</h2>
 * The following methods are available for retrieving beans:
 * <ul>
 *     <li>{@link #getBean(Class) getBean(Class<T> requiredType)}: Retrieves a bean of the specified type from the IoC container.</li>
 *     <li>{@link #getBean(String, Class) getBean(String name, Class<T> requiredType)}: Retrieves a named bean of the specified type
 *     from the IoC container.</li>
 *     <li>{@link #getBeansOfType(Class) getBeansOfType(Class<T> requiredType)}: Retrieves all beans of the specified type from the
 *     IoC container, mapping bean names to their instances.</li>
 *     <li>{@link #getBeans() getBeans()}: Retrieves all registered beans in the IoC container, mapping bean names to their instances.</li>
 * </ul>
 *
 * <p>
 * <b>Note:</b> It is essential for developers to understand the IoC principles and the specific annotations or configurations
 * required for beans to be correctly identified and registered within the IoC container.
 *
 */
public interface BeanFactory {

    /**
     * Scans the specified base package for classes annotated as beans and registers them in the IoC container.
     *
     * @param basePackage The base package to scan for bean classes.
     */
    void registerBeans(String basePackage);

    /**
     * Manually registers the provided classes as beans in the IoC container.
     *
     * @param classes An array of Class objects representing the classes to be registered as beans.
     */
    void registerBeans(Class<?>... classes);

    /**
     * Manually registers a bean with a specified name and its corresponding BeanDefinition.
     *
     * @param beanName       The name under which the bean will be registered.
     * @param beanDefinition The BeanDefinition containing configuration details of the bean.
     */
    void registerBean(String beanName, BeanDefinition beanDefinition);

    /**
     * Retrieves a bean of the specified type from the IoC container.
     *
     * @param requiredType The Class object representing the type of the bean to be retrieved.
     * @param <T>          The type of the bean to be retrieved.
     * @return The bean instance of the specified type.
     */
    <T> T getBean(Class<T> requiredType);

    /**
     * Retrieves a named bean of the specified type from the IoC container.
     *
     * @param name          The name of the bean to be retrieved.
     * @param requiredType  The Class object representing the type of the bean to be retrieved.
     * @param <T>           The type of the bean to be retrieved.
     * @return The bean instance of the specified type with the given name.
     */
    <T> T getBean(String name, Class<T> requiredType);

    /**
     * Retrieves all beans of the specified type from the IoC container, mapping bean names to their instances.
     *
     * @param requiredType The Class object representing the type of the beans to be retrieved.
     * @param <T>          The type of the beans to be retrieved.
     * @return A map of bean names to their instances of the specified type.
     */
    <T> Map<String, T> getBeansOfType(Class<T> requiredType);

    /**
     * Retrieves all registered beans in the IoC container, mapping bean names to their instances.
     *
     * @return A map of all registered bean names to their instances.
     */
    Map<String, Object> getBeans();
}


