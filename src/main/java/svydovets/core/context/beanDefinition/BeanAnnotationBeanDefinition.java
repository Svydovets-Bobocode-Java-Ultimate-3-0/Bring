package svydovets.core.context.beanDefinition;

import java.lang.reflect.Method;

/**
 * Represents a specific type of BeanDefinition associated with Bean Annotations in the Bring framework
 * context.
 * This class extends AbstractBeanDefinition and adds attributes related to bean annotations.
 * <br>
 * * <h2>Bean Definition Attributes:</h2>
 * <strong>configClassName:</strong> Represents the name of the configuration class associated
 * with the bean.<br>
 * <strong>initMethodOfBeanFromConfigClass:</strong> Refers to the initialization method of the bean obtained
 * from the configuration class.<br>
 * <br>
 * This class extends AbstractBeanDefinition and specializes it by adding attributes specific to bean
 * annotations, thereby allowing for a more detailed and specific definition of beans based on their
 * annotations within the framework's context.
 */
public class BeanAnnotationBeanDefinition extends AbstractBeanDefinition {

    private String configClassName;

    private Method initMethodOfBeanFromConfigClass;

    /**
     * Constructs a BeanAnnotationBeanDefinition with a given beanName and beanClass.
     *
     * @param beanName  The name of the bean.
     * @param beanClass The class of the bean.
     */
    public BeanAnnotationBeanDefinition(String beanName, Class<?> beanClass) {
        super(beanName, beanClass);
    }

    /**
     * Retrieves the name of the configuration class associated with the bean.
     *
     * @return The name of the configuration class.
     */
    public String getConfigClassName() {
        return configClassName;
    }

    /**
     * Sets the name of the configuration class associated with the bean.
     *
     * @param configClassName The name of the configuration class to set.
     */
    public void setConfigClassName(String configClassName) {
        this.configClassName = configClassName;
    }

    /**
     * Retrieves the initialization method of the bean obtained from the configuration class.
     *
     * @return The initialization method of the bean from the configuration class.
     */
    public Method getInitMethodOfBeanFromConfigClass() {
        return initMethodOfBeanFromConfigClass;
    }

    /**
     * Sets the initialization method of the bean obtained from the configuration class.
     *
     * @param initMethodOfBeanFromConfigClass The initialization method of the bean to set.
     */
    public void setInitMethodOfBeanFromConfigClass(Method initMethodOfBeanFromConfigClass) {
        this.initMethodOfBeanFromConfigClass = initMethodOfBeanFromConfigClass;
    }
}
