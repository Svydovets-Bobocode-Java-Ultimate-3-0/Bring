package svydovets.core.bpp;

// todo: Maybe it is better to add new annotation @BeanPostProcessor
public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
