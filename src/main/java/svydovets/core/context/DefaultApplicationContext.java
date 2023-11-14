package svydovets.core.context;

import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.Bean;
import svydovets.core.annotation.Configuration;
import svydovets.core.annotation.PostConstruct;
import svydovets.core.annotation.Primary;
import svydovets.core.annotation.Qualifier;
import svydovets.core.bpp.BeanPostProcessor;
import svydovets.core.context.beanDefinition.BeanAnnotationBeanDefinition;
import svydovets.core.context.beanDefinition.BeanDefinition;
import svydovets.core.context.beanDefinition.ComponentAnnotationBeanDefinition;
import svydovets.exception.AutowireBeanException;
import svydovets.exception.BeanCreationException;
import svydovets.exception.InvalidInvokePostConstructMethodException;
import svydovets.exception.NoDefaultConstructor;
import svydovets.exception.NoSuchBeanException;
import svydovets.exception.NoUniqueBeanException;
import svydovets.exception.NoUniquePostConstructException;
import svydovets.util.PackageScanner;
import svydovets.util.ReflectionsUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static svydovets.util.BeanNameResolver.resolveBeanNameByBeanInitMethod;
import static svydovets.util.BeanNameResolver.resolveBeanNameByBeanType;

public class DefaultApplicationContext implements ApplicationContext {
    private final Map<String, Object> beanMap = new HashMap<>();
    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();
    private final PackageScanner packageScanner = new PackageScanner();

    public DefaultApplicationContext(String basePackage) {
        registerBeanDefinitionsForComponentClasses(packageScanner.findAllBeanByBasePackage(basePackage));
        registerBeans();
    }

    public DefaultApplicationContext(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(Configuration.class)) {
            // todo: Think how to process this case
            return;
        }
        Set<Class<?>> beanClasses = packageScanner.findAllBeanByBaseClass(configClass);
        registerBeanDefinitionForConfigClass(configClass);
        registerBeanDefinitionsForComponentClasses(beanClasses);
        registerBeans();
    }

    private void registerBeanDefinitionForConfigClass(Class<?> configClass) {
        beanDefinitionMap.putAll(createBeanDefinitionMapByConfigClass(configClass));
    }

    private void registerBeanDefinitionsForComponentClasses(Set<Class<?>> beanClasses) {
        beanClasses.forEach(this::registerBeanDefinitionForComponentClass);
    }

    private void registerBeanDefinitionForComponentClass(Class<?> beanClass) {
        BeanDefinition beanDefinition = createComponentBeanDefinitionByBeanClass(beanClass);
        beanDefinitionMap.put(beanDefinition.getBeanName(), beanDefinition);
    }

    private BeanDefinition createComponentBeanDefinitionByBeanClass(Class<?> beanClass) {
        ComponentAnnotationBeanDefinition beanDefinition = new ComponentAnnotationBeanDefinition(
                beanClass.getSimpleName(),
                beanClass
        );
        //beanDefinition.setInitializationConstructor(findInitializationConstructor(beanClass));
        beanDefinition.setAutowiredFieldNames(findAutowiredFieldNames(beanClass));
        beanDefinition.setPrimary(beanClass.isAnnotationPresent(Primary.class));
        // todo: Implement BR-20
//        beanDefinition.setScope();
        return beanDefinition;
    }

    private List<String> findAutowiredFieldNames(Class<?> beanClass) {
        return ReflectionsUtil.findAutowiredFieldNames(beanClass);
    }

    private Constructor<?> findInitializationConstructor(Class<?> beanClass) {
        // todo: Implement task BR-16
        throw new UnsupportedOperationException();
    }

    private BeanDefinition createBeanDefinitionByBeanInitMethod(Method beanInitMethod) {
        BeanAnnotationBeanDefinition beanDefinition = new BeanAnnotationBeanDefinition(
                resolveBeanNameByBeanType(beanInitMethod.getReturnType()),
                beanInitMethod.getDeclaringClass());
        // todo: Implement BR-20
//        beanDefinition.setScope();
        beanDefinition.setPrimary(beanInitMethod.isAnnotationPresent(Primary.class));
        beanDefinition.setInitMethodOfBeanFromConfigClass(beanInitMethod);
        beanDefinition.setConfigClassName(resolveBeanNameByBeanInitMethod(beanInitMethod));
        return beanDefinition;
    }

    private Object createBeanFromBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        throw new UnsupportedOperationException();
    }

    private Object postProcessBeforeInitialization(Object bean, String beanName) {
        var beanProcess = bean;
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            beanProcess = beanPostProcessor.postProcessBeforeInitialization(bean, beanName);
        }

        return beanProcess;
    }

    private Object postProcessAfterInitialization(Object bean, String beanName) {
        var beanProcess = bean;
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            beanProcess = beanPostProcessor.postProcessAfterInitialization(bean, beanName);
        }

        return beanProcess;
    }

    private Object initWithBeanPostProcessor(Map.Entry<String, BeanDefinition> entry) {
        var beanName = entry.getKey();
        Object bean = createBeanFromBeanDefinition(beanName, entry.getValue());
        bean = postProcessBeforeInitialization(bean, beanName);

        postConstructInitialization(bean);
        return postProcessAfterInitialization(bean, beanName);
    }

    private void postConstructInitialization(Object bean) {
        Class<?> beanType = bean.getClass();
        Method[] declaredMethods = beanType.getDeclaredMethods();
        Predicate<Method> isAnnotatedMethod = method -> method.isAnnotationPresent(PostConstruct.class);

        boolean isNotUniqueMethod = Arrays.stream(declaredMethods)
                .filter(method -> method.isAnnotationPresent(PostConstruct.class))
                .count() > 1;

        if(isNotUniqueMethod) {
            throw new NoUniquePostConstructException("You cannot have more than one method that is annotated with @PostConstruct.");
        }

        Arrays.stream(declaredMethods)
                .filter(isAnnotatedMethod)
                .findFirst()
                .ifPresent(method -> invokePostConstructMethod(bean, method));
    }

    private static void invokePostConstructMethod(Object bean, Method method) {
        try {
            method.setAccessible(true);
            method.invoke(bean);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new InvalidInvokePostConstructMethodException("Something went wrong. Please check the method that was annotated with @PostConstruct", e);
        }
    }

    private Map<String, BeanDefinition> createBeanDefinitionMapByConfigClass(Class<?> configClass) {
        return Arrays.stream(configClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .map(this::createBeanDefinitionByBeanInitMethod)
                .collect(Collectors.toMap(BeanDefinition::getBeanName, Function.identity()));
    }


    private void registerBeans() {
        beanDefinitionMap.forEach(this::registerBean);
    }

    private Object createBean(Class<?> beanType) {
        // todo: Implement BR-18
        Constructor<?> noArgsConstructor = getPreparedNoArgsConstructor(beanType);
        try {
            Object bean = noArgsConstructor.newInstance();
            populateProperties(bean);
            return bean;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BeanCreationException(String.format("Error creating bean of type %s", beanType.getSimpleName()), e);
        }
    }

    private void registerBean(String beanName, BeanDefinition beanDefinition) {
        // todo: Implement BR-17
        Object bean = createBean(beanDefinition.getBeanClass());
        populateProperties(bean);
        //
        beanMap.putIfAbsent(beanName, bean);
    }

    private Constructor<?> getPreparedNoArgsConstructor(Class<?> beanType) {
        try {
            Constructor<?> constructor = beanType.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException e) {
            throw new NoDefaultConstructor(String.format("No default constructor found of type %s", beanType.getName()));
        }
    }


    @Override
    public <T> T getBean(Class<T> requiredType) {
        Map<String, T> beansOfType = getBeansOfType(requiredType);
        if (beansOfType.size() > 1) {

            List<T> beans = new ArrayList<>(beansOfType.values());

            for(T bean : beans) {
                Class<?> type = bean.getClass();

                if(type.isAnnotationPresent(Primary.class)) {
                    return bean;
                }
            }

            if(requiredType.isAnnotationPresent(Qualifier.class)) {
                var qualifier = requiredType.getDeclaredAnnotation(Qualifier.class);

                String beanName = qualifier.value();

                if(beansOfType.containsKey(beanName)) {
                    return beansOfType.get(beanName);
                }
            }

              throw new NoUniqueBeanException(
                  String.format("No unique bean found of type %s", requiredType.getName()));
        }
        return beansOfType.values().stream().findFirst().orElseThrow(
                () -> new NoSuchBeanException(String.format("No bean found of type %s", requiredType.getName()))
        );
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        Optional<Object> bean = Optional.ofNullable(beanMap.get(name));
        return requiredType.cast(bean.orElseThrow(
                () -> new NoSuchBeanException(String.format("No bean found of type %s", requiredType.getName())))
        );
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> requiredType) {
        return beanMap.entrySet()
                .stream()
                .filter(entry -> requiredType.isAssignableFrom(entry.getValue().getClass()))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> requiredType.cast(entry.getValue())));
    }

    private void populateProperties(Object bean) {
        Field[] fields = bean.getClass().getDeclaredFields();

        // todo: Implement BR-18 (Інжект через сетери має бути перед інжектом через поля)
        for (Field field : fields) {
            boolean isAutowiredPresent = field.isAnnotationPresent(Autowired.class);

            if (isAutowiredPresent) {

                // todo: Implement BR-6
                var dependencyFieldType = field.getType();
                if (Collection.class.isAssignableFrom(dependencyFieldType)) {
                    injectCollectionOfBeans(bean, field, dependencyFieldType);
                } else if (Map.class.isAssignableFrom(dependencyFieldType)) {
                    injectMapOfBeans(bean, field, dependencyFieldType);
                } else {
                    injectBean(bean, field, dependencyFieldType);
                }
            }
        }
    }

    private void injectBean(Object bean, Field fieldForInjection, Class<?> autowireCandidateBeanType) {
        Object autowireCandidate = getBean(autowireCandidateBeanType);
        setDependency(bean, fieldForInjection, autowireCandidate);
    }

    private void injectMapOfBeans(Object bean, Field fieldForInjection, Class<?> mapType) {
        Class<?> autowireCandidateType = retrieveAutowireCandidateType(fieldForInjection);
        var mapOfBeansForInjection = retrieveFieldValue(bean, fieldForInjection);
        var mapOfBeansToInject = getBeansOfType(autowireCandidateType);
        if (mapOfBeansForInjection == null) {
            // Initialize map logic
            setDependency(bean, fieldForInjection, mapOfBeansToInject);
        } else {
            // todo: Implement BR-6
            // todo: CREATE NEW MAP IMPLEMENTATION AND SET TO FIELD!
            setDependency(bean, fieldForInjection, mapOfBeansToInject);
        }
    }

    @SuppressWarnings("unchecked")
    private void injectCollectionOfBeans(Object bean, Field fieldForInjection, Class<?> collectionType) {
        Class<?> autowireCandidateType = retrieveAutowireCandidateType(fieldForInjection);
        Object collectionOfBeansForInjection = retrieveFieldValue(bean, fieldForInjection);
        Collection<?> collectionOfBeansToInject = getBeansOfType(autowireCandidateType).values();
        if (collectionOfBeansForInjection == null) {
            injectCollectionField(bean, fieldForInjection, collectionType, collectionOfBeansToInject);
        } else {
            // Already created via "new" by user
            ((Collection) collectionOfBeansForInjection).addAll(collectionOfBeansToInject);
        }
    }

    private void injectCollectionField(Object bean, Field fieldForInjection, Class<?> collectionType, Collection<?> collectionOfBeansToInject) {
        try {
            fieldForInjection.set(bean, createCollectionInstance(collectionType, collectionOfBeansToInject));
        } catch (IllegalAccessException e) {
            // todo: Implement BR-6
            throw new RuntimeException("");
        }
    }

    private Collection<?> createCollectionInstance(Class<?> collectionType, Collection<?> collectionOfBeansToInject) {
        if (collectionType == List.class) {
            return new ArrayList<>(collectionOfBeansToInject);
        } else if (collectionType == Set.class || collectionType == Collection.class) {
            return new LinkedHashSet<>(collectionOfBeansToInject);
        } else {
            // todo: Implement BR-6
            throw new UnsupportedOperationException("We do not support collection of type: " + collectionType.getName());
        }
    }

    private Object retrieveFieldValue(Object targetBean, Field field) {
        try {
            field.setAccessible(true);
            return field.get(targetBean);
        } catch (IllegalAccessException e) {
            throw new RuntimeException();
        }
    }

    private Type resolveAutowireCandidateGenericType(Type[] genericTypes) {
        // If we got Type[] it is mean that we have at least 1 generic type (even ?),
        // otherwise exception will be thrown above (while casting to "ParameterizedType")
        int size = genericTypes.length;
        if (size == 1) {
            Type singleGenericType = genericTypes[0];
            if (singleGenericType instanceof WildcardType) {
                throw new UnsupportedOperationException("We do not processing wildcard generics!");
            }

            return singleGenericType;
        } else if (size == 2) {
            Type keyGenericType = genericTypes[0];
            if (!keyGenericType.getTypeName().equals(String.class.getName())) {
                throw new UnsupportedOperationException("We processing Map only with String key type");
            }
            return genericTypes[1];
        } else {
            throw new UnsupportedOperationException("What a fuck is it?");
        }
    }

    private Class<?> retrieveAutowireCandidateType(Field fieldForInjection) {
        // May cause ClassCastException: class java.lang.Class cannot be cast to class java.lang.reflect.ParameterizedType
        // The reason is raw generic type. For example, Set set = new HashSet() => field.getGenericType() == Set.class
        try {
            ParameterizedType parameterizedType = (ParameterizedType) fieldForInjection.getGenericType();
            var keyValueGenericTypes = parameterizedType.getActualTypeArguments();
            Type autowireCandidateGenericType = resolveAutowireCandidateGenericType(keyValueGenericTypes);

            return Class.forName(autowireCandidateGenericType.getTypeName());
        } catch (ClassCastException e) {
            // Raw map processing
            // todo: Implement BR-6
            throw new RuntimeException();
        } catch (ClassNotFoundException e) {
            // Class.forName()
            // todo: Implement BR-6
            throw new RuntimeException();
        }
    }

    private void setDependency(Object bean, Field fieldForInjection, Object autowireCandidate) {
        try {
            fieldForInjection.setAccessible(true);
            fieldForInjection.set(bean, autowireCandidate);
        } catch (IllegalAccessException e) {
            throw new AutowireBeanException(String.format("There is access to %s filed", fieldForInjection.getName()));
        }
    }
}
