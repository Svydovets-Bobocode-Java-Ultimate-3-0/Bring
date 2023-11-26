package svydovets.core.context.injector;

import svydovets.exception.AutowireBeanException;
import svydovets.exception.BeanCreationException;
import svydovets.exception.FieldValueIllegalAccessException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

/**
 * The {@code AbstractInjector} class serves as a base class for concrete implementations of the {@code Injector} interface.
 * It provides common functionality for setting dependencies, retrieving field values, and resolving generic types.
 *
 * <p>Concrete injector implementations should extend this class and provide their specific logic for dependency injection.</p>
 *
 * <p>Usage example:</p>
 * <pre>
 *     // Create a custom injector extending AbstractInjector
 *     public class CustomInjector extends AbstractInjector {
 *         // Implement the inject method with custom logic
 *         {@literal @}Override
 *         public void inject(InjectorConfig config) {
 *             // Custom injection logic
 *         }
 *     }
 * </pre>
 *
 * @author Oleksii
 * @see Injector
 * @see InjectorConfig
 */
public abstract class AbstractInjector implements Injector {

    /**
     * Sets the provided dependency on the specified field of the target bean.
     *
     * @param bean               The target bean.
     * @param fieldForInjection The field within the target bean for dependency injection.
     * @param autowireCandidate  The dependency to be injected.
     * @throws AutowireBeanException if there is an issue setting the field due to illegal access.
     */
    protected void setDependency(Object bean, Field fieldForInjection, Object autowireCandidate) {
        try {
            fieldForInjection.setAccessible(true);
            fieldForInjection.set(bean, autowireCandidate);
        } catch (IllegalAccessException e) {
            throw new AutowireBeanException(String.format("There is access to %s field", fieldForInjection.getName()));
        }
    }

    /**
     * Retrieves the current value of the specified field within the target bean.
     *
     * @param targetBean The target bean.
     * @param field      The field for which to retrieve the value.
     * @return The current value of the field.
     * @throws RuntimeException if there is an issue accessing the field.
     */
    protected Object retrieveFieldValue(Object targetBean, Field field) {
        try {
            field.setAccessible(true);
            return field.get(targetBean);
        } catch (IllegalAccessException e) {
            throw new RuntimeException();
        }
    }

    /**
     * Retrieves the type of the autowire candidate for a given field.
     *
     * @param fieldForInjection The field within the target bean for dependency injection.
     * @return The type of the autowire candidate.
     * @throws BeanCreationException if there is an issue resolving the generic type.
     */
    protected Class<?> retrieveAutowireCandidateType(Field fieldForInjection) {
        // May cause ClassCastException: class java.lang.Class cannot be cast to class java.lang.reflect.ParameterizedType
        // The reason is raw generic type. For example, Set set = new HashSet() => field.getGenericType() == Set.class

        try {
            Type autowireCandidateGenericType = resolveAutowireCandidateGenericType(fieldForInjection);
            return Class.forName(autowireCandidateGenericType.getTypeName());
        } catch (ClassNotFoundException e) {
            // Exception thrown by "Class.forName()"
            throw new BeanCreationException(String.format(
                    "Error creating bean of class %s. Please make sure the class is present in the classpath",
                    fieldForInjection.getDeclaringClass().getName())
            );
        }
    }

    private Type resolveAutowireCandidateGenericType(Field fieldForInjection) {
        Type autowireCandidateGenericType = fieldForInjection.getGenericType();
        if (!(autowireCandidateGenericType instanceof ParameterizedType autowireCandidateParameterizedType)) {
            // Raw map processing
            throw new BeanCreationException(String.format(
                    "Don't use raw types for collections. Raw type founded for field %s of %s class",
                    fieldForInjection.getName(),
                    fieldForInjection.getDeclaringClass())
            );
        }
        Type[] genericTypes = autowireCandidateParameterizedType.getActualTypeArguments();
        // If we got Type[] it is mean that we have at least 1 generic type (even ?),
        // otherwise exception will be thrown above (while casting to "ParameterizedType")
        int size = genericTypes.length;
        if (size == 1) {
            Type singleGenericType = genericTypes[0];
            if (singleGenericType instanceof WildcardType) {
                throw new BeanCreationException(String.format(
                        "Don't use wildcard for collections. Wildcard found for bean of type %s",
                        autowireCandidateParameterizedType.getOwnerType())
                );
            }
            return singleGenericType;
        } else if (size == 2) {
            Type mapKeyGenericType = genericTypes[0];
            if (!mapKeyGenericType.getTypeName().equals(String.class.getName())) {
                throw new BeanCreationException("We processing Map only with String key type");
            }
            return genericTypes[1];
        } else {
            throw new UnsupportedOperationException(String.format(
                    "Field %s in %s required a bean of type '%s' that could not be found",
                    fieldForInjection.getName(),
                    fieldForInjection.getDeclaringClass().getName(),
                    fieldForInjection.getType())
            );
        }
    }

}
