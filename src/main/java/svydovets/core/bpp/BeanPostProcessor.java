package svydovets.core.bpp;

/**
 * A {@code BeanPostProcessor} is an interface that defines callback methods
 * to customize the bean instantiation and initialization process.
 *
 * <p>Implementations of this interface can be registered to the IoC container to provide custom
 * processing logic before and after the initialization of a bean. The two primary callback methods
 * are {@code postProcessBeforeInitialization} and {@code postProcessAfterInitialization}.
 *
 * <p>The {@code postProcessBeforeInitialization} method is invoked before the bean's initialization
 * lifecycle phase, allowing for modification or enhancement of the bean instance before any
 * initialization logic is executed. The method returns the potentially modified bean instance.
 *
 * <p>The {@code postProcessAfterInitialization} method is invoked after the bean's initialization
 * phase, allowing for further customization or processing based on the fully initialized bean. The
 * method also returns the potentially modified bean instance.
 *
 * <p>It is important to note that these methods are called for every bean that has been registered.
 * This provides a powerful mechanism for intercepting and customizing the bean lifecycle.
 *
 * <p>By default, both methods return the original, unmodified bean instance, serving as no-op methods.
 * Implementations are free to provide custom logic as needed.
 **
 */
public interface BeanPostProcessor {

    /**
     * Apply custom processing before the initialization of the specified bean.
     *
     * @param bean     the bean instance being processed
     * @param beanName the name of the bean
     * @return the potentially modified bean instance
     */
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    /**
     * Apply custom processing after the initialization of the specified bean.
     *
     * @param bean     the fully initialized bean instance
     * @param beanName the name of the bean
     * @return the potentially modified bean instance
     */
    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}

