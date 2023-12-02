package svydovets.core.context.beanDefinition;

/**
 * <p>
 *     An abstract class representing the basic structure for Bean Definitions within the application context.
 *     Concrete implementations extend this class to define specific bean definitions.
 *     The AbstractBeanDefinition class is designed to serve as a foundational blueprint for defining
 *     various attributes and behaviors of a bean within the context.
 * </p>
 *<br>
 * <h2>Bean Definition Attributes:</h2>
 * <p>
 * <strong>beanName:</strong> Represents the name of the bean within the application context.<br>
 * <strong>beanClass:</strong> Defines the class type associated with the bean.<br>
 * <strong>isPrimary:</strong> Indicates whether the bean is marked as a primary candidate.<br>
 * <strong>scope:</strong> Specifies the scope of the bean (e.g., singleton, prototype, etc.).<br>
 * <strong>creationStatus:</strong> Keeps track of the creation status of the bean.<br>
 *</p>
 *<br>
 * <h2>Interface Implementation:</h2>
 * <p>
 * Implements the BeanDefinition interface, which likely includes methods related to bean definitions and their
 * management within the Bring framework. The purpose of this abstract class is to be extended by concrete
 * implementations that may add more specific functionalities or customize certain behaviors of beans in
 * the context of a Bring framework. Concrete subclasses would inherit these attributes and methods,
 * allowing for consistency and reusability when defining beans with various characteristics within the application
 * context.
 * </p>
 *<br>
 * <p>
 *     Each subclass extending this AbstractBeanDefinition can customize how it handles or utilizes these attributes to
 *     define specific types of beans, manage their lifecycles, and specify their behaviors within the application context.
 *     This separation of concerns allows for a clean and modular approach to handling bean definitions in the framework.
 *</p>
 */
public abstract class AbstractBeanDefinition implements BeanDefinition {

    protected String beanName;

    protected Class<?> beanClass;

    protected boolean isPrimary;

    protected String scope;

    protected String creationStatus;

    /**
     * Constructs an AbstractBeanDefinition with a given name and class.
     *
     * @param beanName  The name of the bean.
     * @param beanClass The class of the bean.
     */
    public AbstractBeanDefinition(String beanName, Class<?> beanClass) {
        this.beanName = beanName;
        this.beanClass = beanClass;
    }

    /**
     * Constructs an empty AbstractBeanDefinition. (Default Constructor)
     */
    public AbstractBeanDefinition() {
        // Empty constructor
    }

    /**
     * Retrieves the name of the bean.
     *
     * @return The name of the bean.
     */
    @Override
    public String getBeanName() {
        return beanName;
    }

    /**
     * Sets the name of the bean.
     *
     * @param beanName The name to set for the bean.
     */
    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    /**
     * Retrieves the class of the bean.
     *
     * @return The class of the bean.
     */
    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    /**
     * Sets the class of the bean.
     *
     * @param beanClass The class to set for the bean.
     */
    @Override
    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    /**
     * Checks if the bean is a primary candidate.
     *
     * @return true if the bean is marked as a primary candidate, otherwise false.
     */
    @Override
    public boolean isPrimary() {
        return isPrimary;
    }

    /**
     * Sets whether the bean is a primary candidate or not.
     *
     * @param primary Boolean value indicating if the bean is a primary candidate.
     */
    @Override
    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    /**
     * Retrieves the scope of the bean.
     *
     * @return The scope of the bean.
     */
    @Override
    public String getScope() {
        return scope;
    }

    /**
     * Sets the scope of the bean.
     *
     * @param scope The scope to set for the bean.
     */
    @Override
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Retrieves the creation status of the bean.
     *
     * @return The creation status of the bean.
     */
    @Override
    public String getCreationStatus() {
        return creationStatus;
    }

    /**
     * Sets the creation status of the bean.
     *
     * @param status The creation status to set for the bean.
     */
    @Override
    public void setCreationStatus(BeanCreationStatus status) {
        this.creationStatus = status.name();
    }
}
