package svydovets.core.context.beanDefinition;

import java.lang.reflect.Constructor;
import java.util.List;

public class ComponentAnnotationBeanDefinition extends AbstractBeanDefinition {
    private Constructor<?> initializationConstructor;
    private List<Class<?>> constructorCandidates;
    private List<String> autowiredFieldNames;

    public Constructor<?> getInitializationConstructor() {
        return initializationConstructor;
    }

    public ComponentAnnotationBeanDefinition(String beanName, Class<?> beanClass) {
        super(beanName, beanClass);
    }

    public void setInitializationConstructor(Constructor<?> initializationConstructor) {
        this.initializationConstructor = initializationConstructor;
    }

    public List<Class<?>> getConstructorCandidates() {
        return constructorCandidates;
    }

    public void setConstructorCandidates(List<Class<?>> constructorCandidates) {
        this.constructorCandidates = constructorCandidates;
    }

    public List<String> getAutowiredFieldNames() {
        return autowiredFieldNames;
    }

    public void setAutowiredFieldNames(List<String> autowiredFieldNames) {
        this.autowiredFieldNames = autowiredFieldNames;
    }
}
