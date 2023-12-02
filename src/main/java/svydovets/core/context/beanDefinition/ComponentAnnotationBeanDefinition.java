package svydovets.core.context.beanDefinition;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Represents a BeanDefinition for components annotated with @Component.
 * This class extends the AbstractBeanDefinition providing additional information such as initialization constructor,
 * constructor candidates, and autowired field names.
 *
 * <br>
 * <h2>How it works:</h2>
 * <p>The class defines instance variables (initializationConstructor, constructorCandidates, and autowiredFieldNames) to hold specific information related to bean definitions annotated with @Component.
 * It provides getter and setter methods for each of these variables to enable accessing and modifying this information from other parts of the framework.
 * When a new instance of ComponentAnnotationBeanDefinition is created by providing a beanName and beanClass, it initializes the superclass AbstractBeanDefinition with these parameters.
 * Developers can use instances of this class to define and manipulate information about beans annotated with @Component, setting and retrieving initialization constructors, constructor candidates, and autowired field names as needed within the framework's context.
 * </p>
 *
 */
public class ComponentAnnotationBeanDefinition extends AbstractBeanDefinition {

    private Constructor<?> initializationConstructor;
    private List<Class<?>> constructorCandidates;
    private List<String> autowiredFieldNames;

    /**
     * Constructs a ComponentAnnotationBeanDefinition instance with the provided bean name and bean class.
     *
     * @param beanName  The name of the bean.
     * @param beanClass The class of the bean.
     */
    public ComponentAnnotationBeanDefinition(String beanName, Class<?> beanClass) {
        super(beanName, beanClass);
    }

    /**
     * Retrieves the initialization constructor associated with this bean definition.
     *
     * @return The initialization constructor.
     */
    public Constructor<?> getInitializationConstructor() {
        return initializationConstructor;
    }

    /**
     * Sets the initialization constructor for this bean definition.
     *
     * @param initializationConstructor The initialization constructor to be set.
     */
    public void setInitializationConstructor(Constructor<?> initializationConstructor) {
        this.initializationConstructor = initializationConstructor;
    }

    /**
     * Retrieves the list of constructor candidates associated with this bean definition.
     *
     * @return The list of constructor candidates.
     */
    public List<Class<?>> getConstructorCandidates() {
        return constructorCandidates;
    }

    /**
     * Sets the list of constructor candidates for this bean definition.
     *
     * @param constructorCandidates The list of constructor candidates to be set.
     */
    public void setConstructorCandidates(List<Class<?>> constructorCandidates) {
        this.constructorCandidates = constructorCandidates;
    }

    /**
     * Retrieves the list of autowired field names associated with this bean definition.
     *
     * @return The list of autowired field names.
     */
    public List<String> getAutowiredFieldNames() {
        return autowiredFieldNames;
    }

    /**
     * Sets the list of autowired field names for this bean definition.
     *
     * @param autowiredFieldNames The list of autowired field names to be set.
     */
    public void setAutowiredFieldNames(List<String> autowiredFieldNames) {
        this.autowiredFieldNames = autowiredFieldNames;
    }
}
