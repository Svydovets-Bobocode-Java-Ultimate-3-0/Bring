package svydovets.core.context.injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svydovets.core.annotation.Qualifier;

import java.lang.reflect.Field;

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

    private static final Logger log = LoggerFactory.getLogger(BeanInjector.class);

    /**
     * Injects a dependency into a specified field of a target bean based on the provided configuration.
     *
     * @param config The configuration specifying the target bean, its field, and the strategy
     *               to obtain the dependency.
     */
    @Override
    public void inject(InjectorConfig config) {
        if(log.isTraceEnabled()) {
            log.trace("Call inject({})", config);
        }

        Field field = config.getBeanField();
        var autowireCandidateType = field.getType();

        Object autowireCandidate;

        if(field.isAnnotationPresent(Qualifier.class)) {
            autowireCandidate = getQualifierCandidate(config, field, autowireCandidateType);
        } else {
            autowireCandidate = config.getBeanReceiver().apply(autowireCandidateType);
        }

        setDependency(config.getBean(), config.getBeanField(), autowireCandidate);
    }

}
