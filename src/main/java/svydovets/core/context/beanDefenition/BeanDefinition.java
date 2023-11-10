package svydovets.core.context.beanDefenition;

import java.util.List;

public interface BeanDefinition {

    Class<?> getBeanClass();

    String getBeanClassName();

    List<Class<?>> getInjectionConstructorBeanClasses();

    void addInjectionConstructorBeanClass(Class<?> beanClass);

    void setInjectionConstructorBeanClasses(List<Class<?>> injectionConstructorBeanClasses);

    List<Class<?>> getInjectionAutowiredBeanClasses();

    void addInjectionAutowiredBeanClass(Class<?> beanClass);

    void setInjectionAutowiredBeanClasses(List<Class<?>> injectionAutowiredBeanClasses);
}
