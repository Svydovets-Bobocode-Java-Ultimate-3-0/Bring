package svydovets.core.context;

import java.util.Map;

/**
 * <p>ApplicationContext - main part of the IoC container.
 * IoC (Inversion of Control) is a design principle which helps to invert the control of object creation and binding.
 * ApplicationContext takes care of code managing the lifecycle and dependencies of the objects.
 * When application starts, it tells ApplicationContext about all its beans and their dependencies.
 * This is done through configuration metadata provided by Java annotations, or Java code.
 * The ApplicationContext then takes control of creating these beans and managing their entire lifecycle.
 * </p>
 *
 * <p>ApplicationContext - a magic box that helps application run smoothly.
 * When application starts, it tells the ApplicationContext about all different beans it has.
 * Each bean has responsibilities, like being saving data.
 * The ApplicationContext keeps track of all these beans and makes sure they are ready to do their job when it needed.
 * It is set up when application starts and then it quietly works in the background.
 * You do not interact with it directly in your daily coding, but it is always there,
 * making sure everything runs smoothly.
 * </p>
 *
 * <p>ApplicationContext cannot be changed while application is running.
 * In case need to update the configuration of application reload ApplicationContext,
 * which means update knowledge of beans.
 * </p>
 *
 * @see AnnotationConfigApplicationContext
 * @see svydovets.web.AnnotationConfigWebApplicationContext
 */
public interface ApplicationContext {

    /**
     * Constant defines scope of bean as singleton.
     */
    String SCOPE_SINGLETON = "singleton";

    /**
     * Constant defines scope of bean as prototype.
     */
    String SCOPE_PROTOTYPE = "prototype";

    /**
     * Retrieve bean by its class type.
     *
     * @param <T>          generic class type of bean
     * @param requiredType class type bean must match
     * @return an instance of the single bean matching the required type
     */
    <T> T getBean(Class<T> requiredType);

    /**
     * Retrieve bean by its name and class type.
     *
     * @param <T>          generic class type of bean
     * @param name         name of bean to retrieve
     * @param requiredType class type bean must match
     * @return an instance of the single bean matching the required type
     */
    <T> T getBean(String name, Class<T> requiredType);

    /**
     * Retrieve beans of a certain type.
     *
     * @param <T>          generic class type of bean
     * @param requiredType class type bean must match
     * @return a map with matching beans, containing bean names as keys and corresponding bean instances as values
     */
    <T> Map<String, T> getBeansOfType(Class<T> requiredType);

    /**
     * Retrieve all beans present in the application context.
     *
     * @return a map with all beans, containing bean names as keys and corresponding bean instances as values
     */
    Map<String, Object> getBeans();
}
