package svydovets.core.context;

import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.Bean;
import svydovets.core.annotation.PostConstruct;
import svydovets.core.annotation.Primary;
import svydovets.core.annotation.Qualifier;
import svydovets.core.annotation.Scope;
import svydovets.core.bpp.BeanPostProcessor;
import svydovets.core.context.beanDefinition.BeanAnnotationBeanDefinition;
import svydovets.core.context.beanDefinition.BeanDefinition;
import svydovets.core.context.beanDefinition.ComponentAnnotationBeanDefinition;
import svydovets.core.context.injector.InjectorConfig;
import svydovets.core.context.injector.InjectorExecutor;
import svydovets.exception.AutowireBeanException;
import svydovets.exception.BeanCreationException;
import svydovets.exception.BeanDefinitionCreateException;
import svydovets.exception.InvalidInvokePostConstructMethodException;
import svydovets.exception.NoDefaultConstructor;
import svydovets.exception.NoSuchBeanDefinitionException;
import svydovets.exception.NoSuchBeanException;
import svydovets.exception.NoUniqueBeanException;
import svydovets.exception.NoUniquePostConstructException;
import svydovets.util.PackageScanner;
import svydovets.util.ReflectionsUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static svydovets.util.BeanNameResolver.resolveBeanName;

public class DefaultApplicationContext implements ApplicationContext {
    public static final String NO_BEAN_FOUND_OF_TYPE = "No bean found of type %s";

    private final Map<String, Object> beanMap = new LinkedHashMap<>();
    private final Map<String, BeanDefinition> beanDefinitionMap = new LinkedHashMap<>();
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();
    private final PackageScanner packageScanner = new PackageScanner();

    public DefaultApplicationContext(String basePackage) {
        registerBeanDefinitionsForComponentClasses(packageScanner.findComponentsByBasePackage(basePackage));
        registerBeans();
    }

    public DefaultApplicationContext(Class<?>... configClasses) {
        Set<Class<?>> beanClasses = packageScanner.findAllBeanByBaseClass(configClasses);
        registerBeanDefinitionForConfigClasses(configClasses);
        registerBeanDefinitionsForComponentClasses(beanClasses);
        registerBeans();
    }

    private void registerBeanDefinitionForConfigClasses(Class<?>... configClasses) {
        // Create bean definition for config class and then for inner beans
        for (Class<?> configClass : configClasses) {
            registerBeanDefinitionForComponentClass(configClass);
            beanDefinitionMap.putAll(createBeanDefinitionMapByConfigClass(configClass));
        }
    }

    private Map<String, BeanDefinition> createBeanDefinitionMapByConfigClass(Class<?> configClass) {
        return Arrays.stream(configClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .map(this::createBeanDefinitionByBeanInitMethod)
                .collect(Collectors.toMap(BeanDefinition::getBeanName, Function.identity()));
    }

    private void registerBeanDefinitionsForComponentClasses(Set<Class<?>> beanClasses) {
        beanClasses.forEach(this::registerBeanDefinitionForComponentClass);
    }

    private void registerBeanDefinitionForComponentClass(Class<?> beanClass) {
        BeanDefinition beanDefinition = createComponentBeanDefinitionByBeanClass(beanClass);
        beanDefinitionMap.put(resolveBeanName(beanClass), beanDefinition);
    }

    private BeanDefinition createComponentBeanDefinitionByBeanClass(Class<?> beanClass) {
        ComponentAnnotationBeanDefinition beanDefinition = new ComponentAnnotationBeanDefinition(
                resolveBeanName(beanClass),
                beanClass
        );
        beanDefinition.setInitializationConstructor(findInitializationConstructor(beanClass));
        beanDefinition.setAutowiredFieldNames(findAutowiredFieldNames(beanClass));
        beanDefinition.setPrimary(beanClass.isAnnotationPresent(Primary.class));
        beanDefinition.setScope(getScopeName(beanClass));

        return beanDefinition;
    }

    private List<String> findAutowiredFieldNames(Class<?> beanClass) {
        return ReflectionsUtil.findAutowiredFieldNames(beanClass);
    }

    private Constructor<?> findInitializationConstructor(Class<?> beanClass) {
        var constructors = Arrays.stream(beanClass.getDeclaredConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(Autowired.class))
                .toList();
        if (constructors.isEmpty()) {
            return getPreparedNoArgsConstructor(beanClass);
        }
        if (constructors.size() == 1) {
            return Optional.of(constructors.get(0))
                    .map(this::prepareConstructor)
                    .orElseThrow();
        }
        throw new BeanDefinitionCreateException(String.format(
                "Error creating bean definition for bean '%s': Invalid autowire-marked constructor: %s. Found constructor with 'required' Autowired annotation already: %s",
                beanClass.getName(),
                constructors.get(1).getName(),
                constructors.get(0).getName())
        );
    }

    private Constructor<?> prepareConstructor(Constructor<?> constructor) {
        constructor.setAccessible(true);
        return constructor;
    }

    private BeanDefinition createBeanDefinitionByBeanInitMethod(Method beanInitMethod) {
        BeanAnnotationBeanDefinition beanDefinition = new BeanAnnotationBeanDefinition(
                resolveBeanName(beanInitMethod),
                beanInitMethod.getReturnType()
        );
        beanDefinition.setScope(getScopeName(beanInitMethod));
        beanDefinition.setPrimary(beanInitMethod.isAnnotationPresent(Primary.class));
        beanDefinition.setInitMethodOfBeanFromConfigClass(beanInitMethod);
        beanDefinition.setConfigClassName(resolveBeanName(beanInitMethod.getDeclaringClass()));
        return beanDefinition;
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

    private Object initWithBeanPostProcessor(String beanName, Object bean) {
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

        if (isNotUniqueMethod) {
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

    private void registerBeans() {
        beanDefinitionMap.forEach(this::registerBean);
        // todo: Autowire properties
//        beanMap.forEach();
        beanMap.forEach(this::initializeBeanAfterRegistering);
    }

    private Object createBean(BeanDefinition beanDefinition) {
        try {
            if (beanDefinition instanceof ComponentAnnotationBeanDefinition componentBeanDefinition) {
                return createComponent(componentBeanDefinition);
            } else {
                return createInnerBeanOfConfigClass((BeanAnnotationBeanDefinition) beanDefinition);
            }
        } catch (Exception e) {
            throw new BeanCreationException(
                    String.format("Error creating bean of type '%s'", beanDefinition.getBeanClass().getName()), e
            );
        }
    }

    private Object createInnerBeanOfConfigClass(BeanAnnotationBeanDefinition beanDefinition) throws InvocationTargetException, IllegalAccessException {
        var configClassName = beanDefinition.getConfigClassName();
        var configClassBeanDefinition = beanDefinitionMap.get(configClassName);
        var configClass = beanMap.get(configClassBeanDefinition.getBeanName());
        if (configClass == null) {
            configClass = createBean(configClassBeanDefinition);
        }
        var initMethod = beanDefinition.getInitMethodOfBeanFromConfigClass();
        Parameter[] parameters = initMethod.getParameters();
        Object[] args = retrieveBeanInitMethodArguments(parameters);

        return prepareMethod(initMethod).invoke(configClass, args);
    }

    private Object[] retrieveBeanInitMethodArguments(Parameter[] parameters) {
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            BeanDefinition parameterBeanDefinition = beanDefinitionMap.get(resolveBeanName(parameter.getType()));
            Object parameterDependency = beanMap.get(parameterBeanDefinition.getBeanName());
            if (parameterDependency == null) {
                createBean(parameterBeanDefinition);
            }
            args[i] = parameterDependency;
        }
        return args;
    }

    private Object createComponent(ComponentAnnotationBeanDefinition beanDefinition) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> initializationConstructor = prepareConstructor(beanDefinition.getInitializationConstructor());
        Class<?>[] autowireCandidateTypes = initializationConstructor.getParameterTypes();
        Object[] autowireCandidates = retrieveAutowireCandidates(autowireCandidateTypes);
        return initializationConstructor.newInstance(autowireCandidates);
    }

    private Object[] retrieveAutowireCandidates(Class<?>[] autowiredCandidateTypes) {
        Object[] autowireCandidates = new Object[autowiredCandidateTypes.length];
        for (int i = 0; i < autowiredCandidateTypes.length; i++) {
            Class<?> autowireCandidateType = autowiredCandidateTypes[i];
            // todo: Check if bean definition is null
            // todo: Need to reuse method "getBean()" with verifying on "Qualifier"
//            BeanDefinition autowireCandidateBeanDefinition = Optional.ofNullable(beanDefinitionMap.get(resolveBeanNameByBeanType(autowireCandidateType)))
//                    .orElseThrow(() -> new NoSuchBeanDefinitionException(
//                            String.format("No bean definition found for type '%s'", autowireCandidateType.getName()))
//                    );
            autowireCandidates[i] = createBeanIfNotPresent(autowireCandidateType);
//            Object autowireCandidate = beanMap.get(autowireCandidateBeanDefinition.getBeanName());
//            if (autowireCandidate == null) {
//                autowireCandidate = createBean(autowireCandidateBeanDefinition);
//            }
//            autowireCandidates[i] = autowireCandidate;
        }
        return autowireCandidates;
    }

    private Method prepareMethod(Method method) {
        method.setAccessible(true);
        return method;
    }

    private void registerBean(String beanName, BeanDefinition beanDefinition) {
        if (beanDefinition.getScope().equals(SCOPE_SINGLETON)) {
            Object bean = createBean(beanDefinition);
            beanMap.putIfAbsent(beanName, bean);
        }
    }

    private void initializeBeanAfterRegistering(String beanName, Object bean) {
        populateProperties(bean);
        beanMap.putIfAbsent(beanName, initWithBeanPostProcessor(beanName, bean));
    }
    private Constructor<?> getPreparedNoArgsConstructor(Class<?> beanType) {
        try {
            Constructor<?> constructor = beanType.getDeclaredConstructor();
            return prepareConstructor(constructor);
        } catch (NoSuchMethodException e) {
            throw new NoDefaultConstructor(String.format("No default constructor found of type %s", beanType.getName()));
        }
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        String beanName = resolveBeanName(requiredType);
        Optional<T> prototypeBean = checkAndCreatePrototypeBean(beanName, requiredType);
        if (prototypeBean.isPresent()) {
            return prototypeBean.get();
        }

        Map<String, T> beansOfType = getBeansOfType(requiredType);
        if (beansOfType.size() > 1) {
            return defineSpecificBean(requiredType, beansOfType);
        }

        return beansOfType.values().stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchBeanException(String.format(NO_BEAN_FOUND_OF_TYPE, requiredType.getName())));
    }

    private <T> T defineSpecificBean(Class<T> requiredType, Map<String, T> beansOfType) {
        if(requiredType.isAnnotationPresent(Qualifier.class)) {
          var qualifier = requiredType.getDeclaredAnnotation(Qualifier.class);

          String beanName = qualifier.value();

          if (beansOfType.containsKey(beanName)) {
            return beansOfType.get(beanName);
          }
        }

        return beansOfType.values()
                .stream()
                .filter(bean -> bean.getClass().isAnnotationPresent(Primary.class))
                .findAny()
                .orElseThrow(() ->
                        new NoUniqueBeanException(String.format("No unique bean found of type %s", requiredType.getName()))
                );
  }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        Optional<T> prototypeBean = checkAndCreatePrototypeBean(name, requiredType);
        if (prototypeBean.isPresent()) {
            return prototypeBean.get();
        }

        Optional<Object> bean = Optional.ofNullable(beanMap.get(name));
        return requiredType.cast(bean.orElseThrow(()
                -> new NoSuchBeanException(String.format(NO_BEAN_FOUND_OF_TYPE, requiredType.getName()))));
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> requiredType) {
        return beanMap.entrySet()
                .stream()
                .filter(entry -> requiredType.isAssignableFrom(entry.getValue().getClass()))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> requiredType.cast(entry.getValue())));
    }

    private <T> Optional<T> checkAndCreatePrototypeBean(String name, Class<T> requiredType) {
        Optional<BeanDefinition> beanDefinitionOptional = Optional.ofNullable(beanDefinitionMap.get(name));
        if (beanDefinitionOptional.isEmpty()) {
            throw new NoSuchBeanException(String.format(NO_BEAN_FOUND_OF_TYPE, requiredType.getName()));
        }

        BeanDefinition beanDefinition = beanDefinitionOptional.get();
        if (beanDefinition.getScope().equals(ApplicationContext.SCOPE_PROTOTYPE)) {
            return Optional.of(requiredType.cast(createBean(beanDefinition)));
        }

        return Optional.empty();
    }

    private void populateProperties(Object bean) {
        Field[] beanFields = bean.getClass().getDeclaredFields();
        for (Field beanField : beanFields) {
            boolean isAutowiredPresent = beanField.isAnnotationPresent(Autowired.class);

            if (isAutowiredPresent) {
                InjectorConfig injectorConfig = InjectorConfig.builder()
                        .withBean(bean)
                        .withBeanField(beanField)
                        .withBeanReceiver(this::getBean)
                        .withBeanOfTypeReceiver(this::getBeansOfType)
                        .build();

                InjectorExecutor.execute(injectorConfig);
            }
        }
    }

    private Object createBeanIfNotPresent(Class<?> beanType) {
        try {
            return getBean(beanType);
        } catch (NoSuchBeanException e) {
            return Optional.ofNullable(beanDefinitionMap.get(resolveBeanName(beanType)))
                    .map(this::createBean)
                    .orElseThrow(() -> new NoSuchBeanDefinitionException(
                            String.format("No bean definition found for type '%s'", beanType.getName()))
                    );
        }
    }

    private String getScopeName(Method beanInitMethod) {
        return beanInitMethod.isAnnotationPresent(Scope.class)
                ? beanInitMethod.getAnnotation(Scope.class).value()
                : ApplicationContext.SCOPE_SINGLETON;
    }

    private String getScopeName(Class<?> beanClass) {
        return beanClass.isAnnotationPresent(Scope.class)
                ? beanClass.getAnnotation(Scope.class).value()
                : ApplicationContext.SCOPE_SINGLETON;
    }
}
