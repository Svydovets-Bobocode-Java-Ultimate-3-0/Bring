package svydovets.core.context.beanDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public interface BeanDefinition {

    String getBeanName();

    void setBeanName(String beanName);

    Class<?> getBeanClass();

    void setBeanClass(Class<?> beanClass);

    List<String> getInjectFieldCandidates();

    void setInjectFieldCandidates(List<String> injectFieldCandidates);

    void setConstructorCandidates(List<Class<?>> constructorCandidates);

    List<Class<?>> getConstructorCandidates();

    boolean isBeanFromConfigClass();

    void setBeanFromConfigClass(boolean beanFromConfigClass);

    Method getInitMethodOfBeanFromConfigClass();

    void setInitMethodOfBeanFromConfigClass(Method initMethodOfBeanFromConfigClass);

    boolean isPrimary();

    void setPrimary(boolean primary);

    void setScope(String scope);

    String getScope();

    String getConfigClassName();

    void setConfigClassName(String configClassName);

    Constructor<?> getPrimaryConstructor();

    void setPrimaryConstructor(Constructor<?> primaryConstructor);
}
