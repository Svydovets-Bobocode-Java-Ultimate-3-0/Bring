package svydovets.core.context.beanDefinition;

import java.lang.reflect.Method;

public class BeanAnnotationBeanDefinition extends AbstractBeanDefinition {

    private String configClassName;

    private Method initMethodOfBeanFromConfigClass;

    public BeanAnnotationBeanDefinition(String beanName, Class<?> beanClass) {
        super(beanName, beanClass);
    }

    public String getConfigClassName() {
        return configClassName;
    }

    public void setConfigClassName(String configClassName) {
        this.configClassName = configClassName;
    }

    public Method getInitMethodOfBeanFromConfigClass() {
        return initMethodOfBeanFromConfigClass;
    }

    public void setInitMethodOfBeanFromConfigClass(Method initMethodOfBeanFromConfigClass) {
        this.initMethodOfBeanFromConfigClass = initMethodOfBeanFromConfigClass;
    }
}
