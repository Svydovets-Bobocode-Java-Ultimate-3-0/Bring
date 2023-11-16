package svydovets.core.context.injector;

import svydovets.exception.AutowireBeanException;
import svydovets.exception.BeanCreationException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

public abstract class AbstractInjector implements Injector {

    protected void setDependency(Object bean, Field fieldForInjection, Object autowireCandidate) {
        try {
            fieldForInjection.setAccessible(true);
            fieldForInjection.set(bean, autowireCandidate);
        } catch (IllegalAccessException e) {
            throw new AutowireBeanException(String.format("There is access to %s filed", fieldForInjection.getName()));
        }
    }

    protected Object retrieveFieldValue(Object targetBean, Field field) {
        try {
            field.setAccessible(true);
            return field.get(targetBean);
        } catch (IllegalAccessException e) {
            throw new RuntimeException();
        }
    }

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
