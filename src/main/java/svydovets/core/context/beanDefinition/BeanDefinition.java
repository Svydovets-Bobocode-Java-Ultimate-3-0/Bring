package svydovets.core.context.beanDefinition;

public interface BeanDefinition {

    String getBeanName();

    void setBeanName(String beanName);

    Class<?> getBeanClass();

    void setBeanClass(Class<?> beanClass);

    boolean isPrimary();

    void setPrimary(boolean primary);

    void setScope(String scope);

    String getScope();

    String getCreationStatus();

    void setCreationStatus(BeanCreationStatus status);

    enum BeanCreationStatus {
        CREATED,
        NOT_CREATED,
        IN_PROGRESS
    }
}
