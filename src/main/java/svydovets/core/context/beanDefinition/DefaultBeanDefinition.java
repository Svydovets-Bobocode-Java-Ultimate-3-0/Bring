package svydovets.core.context.beanDefinition;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

public class DefaultBeanDefinition implements BeanDefinition {

    private Class<?> beanClass;

    private List<Class<?>> injectionConstructorBeanClasses;

    private List<Class<?>> injectionAutowiredBeanClasses;

    @Override
    public String getBeanClassName() {
        return beanClass.getName();
    }

    @Override
    public void addInjectionConstructorBeanClass(Class<?> beanClass) {
        Objects.requireNonNull(beanClass);
        injectionConstructorBeanClasses.add(beanClass);
    }

    @Override
    public void addInjectionAutowiredBeanClass(Class<?> beanClass) {
        Objects.requireNonNull(beanClass);
        injectionAutowiredBeanClasses.add(beanClass);
    }

}
