package svydovets.core.context.beanFactory;

import svydovets.core.annotation.PostConstruct;
import svydovets.core.annotation.Primary;
import svydovets.core.annotation.Qualifier;
import svydovets.core.bpp.AutowiredAnnotationBeanPostProcessor;
import svydovets.core.bpp.BeanPostProcessor;
import svydovets.core.context.ApplicationContext;
import svydovets.core.context.beanDefinition.BeanAnnotationBeanDefinition;
import svydovets.core.context.beanDefinition.BeanDefinition;
import svydovets.core.context.beanDefinition.BeanDefinitionFactory;
import svydovets.core.context.beanDefinition.ComponentAnnotationBeanDefinition;
import svydovets.core.context.beanFactory.command.CommandFactory;
import svydovets.core.context.beanFactory.command.CommandFunctionName;
import svydovets.exception.*;
import svydovets.util.PackageScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static svydovets.core.context.ApplicationContext.SCOPE_SINGLETON;
import static svydovets.util.BeanNameResolver.resolveBeanName;
import static svydovets.util.ReflectionsUtil.prepareConstructor;
import static svydovets.util.ReflectionsUtil.prepareMethod;

public class BeanFactory {
    public static final String NO_BEAN_FOUND_OF_TYPE = "No bean found of type %s";
    public static final String NO_UNIQUE_BEAN_FOUND_OF_TYPE = "No unique bean found of type %s";
    private final Map<String, Object> beanMap = new LinkedHashMap<>();
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();
    private final PackageScanner packageScanner = new PackageScanner();
    private final BeanDefinitionFactory beanDefinitionFactory = new BeanDefinitionFactory();

    private CommandFactory commandFactory = new CommandFactory();

    public BeanFactory() {
        commandFactory.registryCommand(CommandFunctionName.FC_GET_BEAN, this::getBean);
        commandFactory.registryCommand(CommandFunctionName.FC_GET_BEANS_OF_TYPE, this::getBeansOfType);
        beanPostProcessors.add(new AutowiredAnnotationBeanPostProcessor(commandFactory));
    }

    public void registerBeans(String basePackage) {
        Set<Class<?>> beanClasses = packageScanner.findComponentsByBasePackage(basePackage);  //TODO why dont search Components + Configurations ? (like below)
        doRegisterBeans(beanClasses);
    }

    public void registerBeans(Class<?>... classes) {
        Set<Class<?>> beanClasses = packageScanner.findAllBeanCandidatesByBaseClass(classes);
        doRegisterBeans(beanClasses);
    }

    private void doRegisterBeans(Set<Class<?>> beanClasses) {
        beanDefinitionFactory
                .registerBeanDefinitions(beanClasses)
                .forEach(this::registerBean);
        beanMap.forEach(this::initializeBeanAfterRegistering);
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
            prepareMethod(method).invoke(bean);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new InvalidInvokePostConstructMethodException("Something went wrong. Please check the method that was annotated with @PostConstruct", e);
        }
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
        var configClassBeanDefinition = beanDefinitionFactory.getBeanDefinitionByBeanName(configClassName);
        var configClass = beanMap.get(configClassBeanDefinition.getBeanName());
        if (configClass == null) {
            configClass = createBean(configClassBeanDefinition);
        }
        var initMethod = beanDefinition.getInitMethodOfBeanFromConfigClass();
        Object[] args = retrieveBeanInitMethodArguments(initMethod);

        return prepareMethod(initMethod).invoke(configClass, args);
    }

    private Object[] retrieveBeanInitMethodArguments(Method initMethod) {
        Parameter[] parameters = initMethod.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            BeanDefinition parameterBeanDefinition = beanDefinitionFactory.getBeanDefinitionByBeanName(resolveBeanName(parameter.getType()));
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
        Object[] autowireCandidates = retrieveAutowireCandidates(initializationConstructor);
        return initializationConstructor.newInstance(autowireCandidates);
    }

    private Object[] retrieveAutowireCandidates(Constructor<?> initializationConstructor) {
        Class<?>[] autowireCandidateTypes = initializationConstructor.getParameterTypes();
        Object[] autowireCandidates = new Object[autowireCandidateTypes.length];
        for (int i = 0; i < autowireCandidateTypes.length; i++) {
            Class<?> autowireCandidateType = autowireCandidateTypes[i];
            autowireCandidates[i] = createBeanIfNotPresent(autowireCandidateType);
        }
        return autowireCandidates;
    }

    private void registerBean(String beanName, BeanDefinition beanDefinition) {
        if (beanDefinition.getScope().equals(SCOPE_SINGLETON)) {
            Object bean = createBean(beanDefinition);
            beanMap.putIfAbsent(beanName, bean);
        }
    }

    private void initializeBeanAfterRegistering(String beanName, Object bean) {
        beanMap.putIfAbsent(beanName, initWithBeanPostProcessor(beanName, bean));
    }


    public <T> T getBean(Class<T> requiredType) {
        Map<String, T> beansOfType = getBeansOfType(requiredType);
        if (beansOfType.isEmpty()) {
            String beanName = resolveBeanName(requiredType);
            Optional<T> prototypeBean = checkAndCreatePrototypeBean(beanName, requiredType);
            if (prototypeBean.isPresent()) {
                return prototypeBean.get();
            }
        } else if (beansOfType.size() > 1) {
            return defineSpecificBean(requiredType, beansOfType);
        }

        return beansOfType.values().stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchBeanException(String.format(NO_BEAN_FOUND_OF_TYPE, requiredType.getName())));
    }

    private <T> T defineSpecificBean(Class<T> requiredType, Map<String, T> beansOfType) {
        if (requiredType.isAnnotationPresent(Qualifier.class)) {
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
                        new NoUniqueBeanException(String.format(NO_UNIQUE_BEAN_FOUND_OF_TYPE, requiredType.getName()))
                );
    }

    public <T> T getBean(String name, Class<T> requiredType) {
        Optional<Object> bean = Optional.ofNullable(beanMap.get(name));
        Object beanObject = bean.orElse(checkAndCreatePrototypeBean(name, requiredType).orElseThrow(()
                -> new NoSuchBeanException(String.format(NO_BEAN_FOUND_OF_TYPE, requiredType.getName()))));

        return requiredType.cast(beanObject);
    }

    public <T> Map<String, T> getBeansOfType(Class<T> requiredType) {
        return beanMap.entrySet()
                .stream()
                .filter(entry -> requiredType.isAssignableFrom(entry.getValue().getClass()))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> requiredType.cast(entry.getValue())));
    }

    private <T> Optional<T> checkAndCreatePrototypeBean(String name, Class<T> requiredType) {
        Optional<BeanDefinition> beanDefinitionOptional = Optional
                .ofNullable(beanDefinitionFactory.getBeanDefinitionByBeanName(name));
        if (beanDefinitionOptional.isEmpty()) {
            throw new NoSuchBeanException(String.format(NO_BEAN_FOUND_OF_TYPE, requiredType.getName()));
        }

        BeanDefinition beanDefinition = beanDefinitionOptional.get();
        if (beanDefinition.getScope().equals(ApplicationContext.SCOPE_PROTOTYPE)) {
            return Optional.of(requiredType.cast(createBean(beanDefinition)));
        }

        return Optional.empty();
    }

    private Stream<Object> getBeanForSetterMethod(Class<?>[] parameterTypes) {
        return Arrays.stream(parameterTypes)
                .map(this::getBean);
    }

    private static void invokeSetterMethod(List<Method> methods, Object targetBean, Object[] injectBeans) {
        try {
            for (Method method : methods) {
                method.setAccessible(true);
                method.invoke(targetBean, injectBeans);
            }

        } catch (IllegalAccessException | InvocationTargetException e){
            throw new AutowireBeanException("There is no access to method");
        }
    }

    private Object createBeanIfNotPresent(Class<?> beanType) {
        try {
            return getBean(beanType);
        } catch (NoSuchBeanException e) {
            return Optional.ofNullable(beanDefinitionFactory.getBeanDefinitionByBeanName(resolveBeanName(beanType)))
                    .map(this::createBean)
                    .orElseThrow(() -> new NoSuchBeanDefinitionException(
                            String.format("No bean definition found for type '%s'", beanType.getName()))
                    );
        }
    }
}
