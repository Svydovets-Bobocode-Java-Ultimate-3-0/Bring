package svydovets.core.context.beanDefinition;

/**
 * The BeanDefinition interface represents the blueprint for defining attributes and behaviors
 * of beans within the application context in Spring-like frameworks.
 * The purpose of the BeanDefinition interface is to define a contract that encapsulates the fundamental
 * attributes and operations necessary for defining and managing beans within the application context
 * of a Bring framework.
 */
public interface BeanDefinition {

    /**
     * Retrieves the name of the bean.
     *
     * @return The name of the bean.
     */
    String getBeanName();

    /**
     * Sets the name of the bean.
     *
     * @param beanName The name to set for the bean.
     */
    void setBeanName(String beanName);

    /**
     * Retrieves the class of the bean.
     *
     * @return The class of the bean.
     */
    Class<?> getBeanClass();

    /**
     * Sets the class of the bean.
     *
     * @param beanClass The class to set for the bean.
     */
    void setBeanClass(Class<?> beanClass);

    /**
     * Checks if the bean is a primary candidate.
     *
     * @return true if the bean is marked as a primary candidate, otherwise false.
     */
    boolean isPrimary();

    /**
     * Sets whether the bean is a primary candidate or not.
     *
     * @param primary Boolean value indicating if the bean is a primary candidate.
     */
    void setPrimary(boolean primary);

    /**
     * Retrieves the scope of the bean.
     *
     * @return The scope of the bean.
     */
    String getScope();

    /**
     * Sets the scope of the bean.
     *
     * @param scope The scope to set for the bean.
     */
    void setScope(String scope);

    /**
     * Retrieves the creation status of the bean.
     *
     * @return The creation status of the bean.
     */
    String getCreationStatus();

    /**
     * Sets the creation status of the bean.
     *
     * @param status The creation status to set for the bean.
     */
    void setCreationStatus(BeanCreationStatus status);

    /**
     * Enum representing the possible creation statuses of a bean.
     */
    enum BeanCreationStatus {
        CREATED,
        NOT_CREATED,
        IN_PROGRESS
    }
}