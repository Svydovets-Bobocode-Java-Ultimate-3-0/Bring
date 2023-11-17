package svydovets.core.context.injector;

/**
 * Implementation of {@link Injector} that represents injection to beans.
 *
 * <p>The {@code BeanInjector} class is an implementation of the {@code AbstractInjector} class,
 * designed to inject dependencies into beans.
 *
 * <p>Usage example:
 *
 * <pre>
 *     BeanInjector beanInjector = new BeanInjector();
 *     InjectorConfig config = new InjectorConfig(...);
 *     beanInjector.inject(config);
 * </pre>
 *
 * @author Oleksii
 * @see AbstractInjector
 * @see InjectorConfig
 */
public class BeanInjector extends AbstractInjector {

    /**
     * Injects a dependency into a specified field of a target bean based on the provided configuration.
     *
     * @param config The configuration specifying the target bean, its field, and the strategy
     *               to obtain the dependency.
     */
    @Override
    public void inject(InjectorConfig config) {
        var autowireCandidateType = config.getBeanField().getType();

        Object autowireCandidate = config.getBeanReceiver().apply(autowireCandidateType);

        setDependency(config.getBean(), config.getBeanField(), autowireCandidate);
    }

}
