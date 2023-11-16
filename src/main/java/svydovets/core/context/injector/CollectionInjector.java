package svydovets.core.context.injector;

import svydovets.exception.BeanCreationException;

import java.lang.reflect.Field;
import java.util.*;

public class CollectionInjector extends AbstractInjector {
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
