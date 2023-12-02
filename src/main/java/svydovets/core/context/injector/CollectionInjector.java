package svydovets.core.context.injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svydovets.core.exception.BeanCreationException;
import svydovets.core.exception.InjectCollectionFieldException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static svydovets.util.ErrorMessageConstants.ERROR_NOT_SUPPORT_DEPENDENCY_INJECT_TO_COLLECTION;

/**
 * The {@code CollectionInjector} class is an implementation of the {@code AbstractInjector}
 * designed specifically for injecting dependencies into collections (e.g., List, Set) within a target bean.
 *
 * <p>This injector uses a configuration provided through an {@code InjectorConfig} object
 * to determine the target bean, its field, and the strategy to obtain the dependencies
 * for injection into the collection. The actual injection is performed by invoking the {@code inject} method.</p>
 *
 * <p>The injection process involves retrieving the type of the collection field, obtaining
 * the existing collection from the target bean, creating a new collection instance if needed,
 * and injecting the dependencies into the collection. The resulting collection is then set on the target bean's field.</p>
 *
 * <p>Usage example:</p>
 * <pre>
 *     CollectionInjector collectionInjector = new CollectionInjector();
 *     InjectorConfig config = new InjectorConfig(...);
 *     collectionInjector.inject(config);
 * </pre>
 *
 * @author Oleksii
 * @see AbstractInjector
 * @see InjectorConfig
 */
public class CollectionInjector extends AbstractInjector {

    private static final Logger log = LoggerFactory.getLogger(CollectionInjector.class);

    /**
     * Injects dependencies into a collection field of a target bean based on the provided configuration.
     * If the collection field is already created by the user, the dependencies are added to the existing collection.
     * Otherwise, a new collection instance is created, and the dependencies are injected into it.
     *
     * @param config The configuration specifying the target bean, its collection field, and the strategy
     *               to obtain the dependencies.
     * @throws BeanCreationException if the collection type is not supported.
     */
    @Override
    public void inject(InjectorConfig config) {
        log.trace("Call inject({})", config);

        Class<?> autowireCandidateType = retrieveAutowireCandidateType(config.getBeanField());
        Object collectionOfBeansForInjection = retrieveFieldValue(config.getBean(), config.getBeanField());

        Collection<?> collectionOfBeansToInject = config.getBeanOfTypeReceiver().apply(autowireCandidateType).values();

        if (collectionOfBeansForInjection == null) {
            injectCollectionField(config.getBean(), config.getBeanField(), collectionOfBeansToInject);
        } else {
            ((Collection) collectionOfBeansForInjection).addAll(collectionOfBeansToInject);
        }
    }

    @SuppressWarnings("unchecked")
    private void injectCollectionField(Object bean, Field fieldForInjection, Collection<?> collectionOfBeansToInject) {
        log.trace("Call injectCollectionField({}, {}, {})", bean, fieldForInjection, collectionOfBeansToInject);

        try {
            Collection<?> collectionOfBeans = createCollectionInstance(fieldForInjection.getType());
            collectionOfBeans.addAll((Collection) collectionOfBeansToInject);
            fieldForInjection.set(bean, collectionOfBeans);
        } catch (IllegalAccessException exception) {
            throw new InjectCollectionFieldException(exception.getMessage(), exception);
        }
    }

    private Collection<?> createCollectionInstance(Class<?> collectionType) {
        if (collectionType == List.class) {
            return new ArrayList<>();
        } else if (collectionType == Set.class || collectionType == Collection.class) {
            return new LinkedHashSet<>();
        } else {
            throw new BeanCreationException(
                String.format(ERROR_NOT_SUPPORT_DEPENDENCY_INJECT_TO_COLLECTION, collectionType.getName())
            );
        }
    }

}
