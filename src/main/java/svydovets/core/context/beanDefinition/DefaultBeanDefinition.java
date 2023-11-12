package svydovets.core.context.beanDefinition;


import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public class DefaultBeanDefinition implements BeanDefinition {
    private String beanName;

    private Class<?> beanClass;

    private List<String> injectFieldCandidates;

    Constructor<?> primaryConstructor;

    private List<Class<?>> constructorCandidates;

    private boolean isBeanFromConfigClass;

    // Був Class<?> configClass
    private String configClassName;

    private boolean isConfigClass;

    private Method initMethodOfBeanFromConfigClass;

    private boolean isPrimary;

    private String scope;

    public DefaultBeanDefinition() {
        //empty constructor
    }

    public DefaultBeanDefinition(Class<?> beanClass, String beanName) {
        this.beanClass = beanClass;
        this.beanName = beanName;
    }

    @Override
    public String getBeanName() {
        return beanName;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Override
    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    public List<String> getInjectFieldCandidates() {
        return injectFieldCandidates;
    }

    @Override
    public void setInjectFieldCandidates(List<String> injectFieldCandidates) {
        this.injectFieldCandidates = injectFieldCandidates;
    }

    @Override
    public Constructor<?> getPrimaryConstructor() {
        return primaryConstructor;
    }

    @Override
    public void setPrimaryConstructor(Constructor<?> primaryConstructor) {
        this.primaryConstructor = primaryConstructor;
    }

    @Override
    public List<Class<?>> getConstructorCandidates() {
        return constructorCandidates;
    }

    @Override
    public void setConstructorCandidates(List<Class<?>> constructorCandidates) {
        this.constructorCandidates = constructorCandidates;
    }

    @Override
    public boolean isBeanFromConfigClass() {
        return isBeanFromConfigClass;
    }

    @Override
    public void setBeanFromConfigClass(boolean beanFromConfigClass) {
        isBeanFromConfigClass = beanFromConfigClass;
    }

    @Override
    public String getConfigClassName() {
        return configClassName;
    }

    @Override
    public void setConfigClassName(String configClassName) {
        this.configClassName = configClassName;
    }

    public boolean isConfigClass() {
        return isConfigClass;
    }

    public void setConfigClass(boolean configClass) {
        isConfigClass = configClass;
    }

    @Override
    public Method getInitMethodOfBeanFromConfigClass() {
        return initMethodOfBeanFromConfigClass;
    }

    @Override
    public void setInitMethodOfBeanFromConfigClass(Method initMethodOfBeanFromConfigClass) {
        this.initMethodOfBeanFromConfigClass = initMethodOfBeanFromConfigClass;
    }

    @Override
    public boolean isPrimary() {
        return isPrimary;
    }

    @Override
    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public void setScope(String scope) {
        this.scope = scope;
    }
}
