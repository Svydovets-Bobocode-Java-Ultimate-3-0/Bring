package svydovets.core.context.injector;

import svydovets.exception.BeanCreationException;

import java.lang.reflect.Field;
import java.util.*;

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
        Class<?> autowireCandidateType = retrieveAutowireCandidateType(config.getBeanField());
        Object collectionOfBeansForInjection = retrieveFieldValue(config.getBean(), config.getBeanField());

        Collection<?> collectionOfBeansToInject = config.getBeanOfTypeReceiver().apply(autowireCandidateType).values();

        if (collectionOfBeansForInjection == null) {
            injectCollectionField(config.getBean(), config.getBeanField(), collectionOfBeansToInject);
        } else {
            // Already created via "new" by user
            ((Collection) collectionOfBeansForInjection).addAll(collectionOfBeansToInject);
        }
    }

    @SuppressWarnings("unchecked")
    private void injectCollectionField(Object bean, Field fieldForInjection, Collection<?> collectionOfBeansToInject) {
        try {
            Collection<?> collectionOfBeans = createCollectionInstance(fieldForInjection.getType());
            collectionOfBeans.addAll((Collection) collectionOfBeansToInject);
            fieldForInjection.set(bean, collectionOfBeans);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("");
        }
    }

    private Collection<?> createCollectionInstance(Class<?> collectionType) {
        if (collectionType == List.class) {
            return new ArrayList<>();
        } else if (collectionType == Set.class || collectionType == Collection.class) {
            return new LinkedHashSet<>();
        } else {
            throw new BeanCreationException(String.format(
                    "We don't support dependency injection into collection of type: %s",
                    collectionType.getName())
            );
        }
    }

}
