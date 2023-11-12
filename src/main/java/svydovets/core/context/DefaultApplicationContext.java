package svydovets.core.context;

import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.Bean;
import svydovets.core.bpp.BeanPostProcessor;
import svydovets.core.context.beanDefinition.BeanDefinition;
import svydovets.core.context.beanDefinition.DefaultBeanDefinition;
import svydovets.exception.*;
import svydovets.util.BeanNameResolver;
import svydovets.util.ReflectionsUtil;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static svydovets.util.BeanNameResolver.resolveBeanNameByBeanInitMethod;
import static svydovets.util.BeanNameResolver.resolveBeanNameByBeanType;

public class DefaultApplicationContext implements ApplicationContext {
    private final Map<String, Object> beanMap = new HashMap<>();
    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    public DefaultApplicationContext(String basePackage) {
        registerBeans(ReflectionsUtil.findAllBeanByBasePackage(basePackage));
        populateProperties();
    }

    public DefaultApplicationContext(Class<?> configClass) {
        // todo 1): В цьому сеті зберігаються класи з аннотацією @Component (з проскановаго пакету @ComponentScan)
        Set<Class<?>> beanClasses = ReflectionsUtil.findAllBeanByBaseClass(configClass);
        //can be @Configuration in backage - should be recursive
        // todo 2): Додаємо конфіг клас в загальний сет "beanClasses"
        beanClasses.add(configClass);
        // todo 3): Створюємо bean definition по загальному сету "beanClasses"
        beanDefinitionMap.putAll(createBeanDefinitionMapBySetOfBeanClasses(beanClasses));
        // todo 4): Опрацьовуємо всі методи конфіг класа з анотацією @Bean - створюємо їх bean definitions
        beanDefinitionMap.putAll(createBeanDefinitionMapByConfigClass(configClass));

        // todo 5): Починаємо створювати біни
        // Clear map before first initialize ???
        beanMap.clear();
        var beans = beanDefinitionMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, this::initWithBeanPostProcessor));
        beanMap.putAll(beans);

    }

    private Map<String, DefaultBeanDefinition> createBeanDefinitionMapBySetOfBeanClasses(Set<Class<?>> beanClasses) {
        return beanClasses.stream()
                .map(beanClass -> new DefaultBeanDefinition(beanClass, resolveBeanNameByBeanType(beanClass)))
                .collect(Collectors.toMap(BeanDefinition::getBeanName, Function.identity()));
    }

    private BeanDefinition createBeanDefinitionByBeanInitMethod(Method beanInitMethod) {
        BeanDefinition beanDefinition = new DefaultBeanDefinition();
        beanDefinition.setBeanClass(beanInitMethod.getReturnType());
        beanDefinition.setBeanName(resolveBeanNameByBeanInitMethod(beanInitMethod));
        beanDefinition.setConfigClassName(resolveBeanNameByBeanType(beanInitMethod.getDeclaringClass()));
        beanDefinition.setBeanFromConfigClass(true);
        beanDefinition.setInitMethodOfBeanFromConfigClass(beanInitMethod);

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
        // todo: implement postConstructInitialization(bean) method for @PostConstruct annotation
        //  (Можливо треба додати поле "Method postConstructMethod" в bean definition)
        postConstructInitialization(bean);
        return postProcessAfterInitialization(bean, beanName);
    }

    private void postConstructInitialization(Object bean) {
        throw new UnsupportedOperationException();
    }

    private Map<String, BeanDefinition> createBeanDefinitionMapByConfigClass(Class<?> configClass) {
        return Arrays.stream(configClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .map(this::createBeanDefinitionByBeanInitMethod)
                .collect(Collectors.toMap(BeanDefinition::getBeanName, Function.identity()));
    }


    private void registerBeans(Set<Class<?>> beanTypes) {
        beanTypes.forEach(this::registerBean);
    }

    private Object createBean(Class<?> beanType) {
        Constructor<?> noArgsConstructor = getPreparedNoArgsConstructor(beanType);
        try {
            return noArgsConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BeanCreationException(String.format("Error creating bean of type %s", beanType.getSimpleName()), e);
        }
    }

    private void registerBean(Class<?> beanType) {
        Object bean = createBean(beanType);
        String beanName = beanType.getSimpleName();
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
            throw new NoUniqueBeanException(String.format("No unique bean found of type %s", requiredType.getName()));
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

    private void populateProperties() {
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            Object bean = entry.getValue();
            Field[] fields = bean.getClass().getDeclaredFields();

            for (Field field : fields) {
                boolean isAutowiredPresent = field.isAnnotationPresent(Autowired.class);

                if (isAutowiredPresent) {
                    var fieldType = field.getType();
                    if (Collection.class.isAssignableFrom(fieldType)) {
                        injectCollectionOfBeans(bean, field, fieldType);
                    } else if (Map.class.isAssignableFrom(fieldType)) {
                        injectMapOfBeans(bean, field, fieldType);
                    } else {
                        injectBean(bean, field, fieldType);
                    }
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
            // todo:
            throw new RuntimeException("");
        }
    }

    private Collection<?> createCollectionInstance(Class<?> collectionType, Collection<?> collectionOfBeansToInject) {
        if (collectionType == List.class) {
            return new ArrayList<>(collectionOfBeansToInject);
        } else if (collectionType == Set.class || collectionType == Collection.class) {
            return new LinkedHashSet<>(collectionOfBeansToInject);
        } else {
            // todo:
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
            // todo:
            throw new RuntimeException();
        } catch (ClassNotFoundException e) {
            // Class.forName()
            // todo:
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
