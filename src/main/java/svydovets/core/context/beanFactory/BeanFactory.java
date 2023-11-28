package svydovets.core.context.beanFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svydovets.core.annotation.PostConstruct;
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
import svydovets.exception.BeanCreationException;
import svydovets.exception.InvalidInvokePostConstructMethodException;
import svydovets.exception.NoSuchBeanDefinitionException;
import svydovets.exception.NoUniqueBeanDefinitionException;
import svydovets.exception.NoUniqueBeanException;
import svydovets.exception.NoUniquePostConstructException;
import svydovets.util.BeanNameResolver;
import svydovets.util.ErrorMessageConstants;
import svydovets.util.PackageScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static svydovets.util.BeanNameResolver.resolveBeanName;
import static svydovets.util.ErrorMessageConstants.ERROR_CREATED_BEAN_OF_TYPE;
import static svydovets.util.ErrorMessageConstants.ERROR_NOT_UNIQUE_METHOD_THAT_ANNOTATED_POST_CONSTRUCT;
import static svydovets.util.ErrorMessageConstants.ERROR_THE_METHOD_THAT_WAS_ANNOTATED_WITH_POST_CONSTRUCT;
import static svydovets.util.ErrorMessageConstants.NO_BEAN_DEFINITION_FOUND_OF_TYPE;
import static svydovets.util.ErrorMessageConstants.NO_BEAN_FOUND_OF_TYPE;
import static svydovets.util.ErrorMessageConstants.NO_UNIQUE_BEAN_FOUND_OF_TYPE;
import static svydovets.util.ReflectionsUtil.prepareConstructor;
import static svydovets.util.ReflectionsUtil.prepareMethod;


/**
 * Public API
 */
public class BeanFactory {

    public static final Set<String> SUPPORTED_SCOPES = new HashSet<>(Arrays.asList(
            ApplicationContext.SCOPE_SINGLETON,
            ApplicationContext.SCOPE_PROTOTYPE
    ));

    private static final Logger log = LoggerFactory.getLogger(BeanFactory.class);

    private final Map<String, Object> beanMap = new LinkedHashMap<>();

    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    private final PackageScanner packageScanner = new PackageScanner();

    private final BeanDefinitionFactory beanDefinitionFactory = new BeanDefinitionFactory();

    private final CommandFactory commandFactory = new CommandFactory();

    public BeanFactory() {
        commandFactory.registryCommand(CommandFunctionName.FC_GET_BEAN, this::createBeanIfNotPresent);
        commandFactory.registryCommand(CommandFunctionName.FC_GET_BEANS_OF_TYPE, this::getBeansOfType);
        beanPostProcessors.add(new AutowiredAnnotationBeanPostProcessor(commandFactory));
    }

    public void registerBeans(String basePackage) {
        log.info("Scanning package: {}", basePackage);
        Set<Class<?>> beanClasses = packageScanner.findComponentsByBasePackage(basePackage);  //TODO why dont search Components + Configurations ? (like below)
        log.info("Registering beans");
        doRegisterBeans(beanClasses);
    }

    public void registerBeans(Class<?>... classes) {
        Set<Class<?>> beanClasses = packageScanner.findAllBeanCandidatesByBaseClass(classes);
        doRegisterBeans(beanClasses);
    }

    private void doRegisterBeans(Set<Class<?>> beanClasses) {
        log.trace("Call doRegisterBeans({})", beanClasses);
        beanDefinitionFactory
                .registerBeanDefinitions(beanClasses)
                .forEach(this::registerBean);

        log.info("Beans post processing");

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

    private void postConstructInitialization(Object bean) throws NoUniquePostConstructException {
        Class<?> beanType = bean.getClass();
        Method[] declaredMethods = beanType.getDeclaredMethods();
        Predicate<Method> isAnnotatedMethod = method -> method.isAnnotationPresent(PostConstruct.class);

        // todo: Refactor a little bit. Collect methods to list to avoid second filtering
        boolean isNotUniqueMethod = Arrays.stream(declaredMethods).filter(isAnnotatedMethod).count() > 1;
        if (isNotUniqueMethod) {
            log.error(ERROR_NOT_UNIQUE_METHOD_THAT_ANNOTATED_POST_CONSTRUCT);

            throw new NoUniquePostConstructException(ERROR_NOT_UNIQUE_METHOD_THAT_ANNOTATED_POST_CONSTRUCT);
        }

        // todo: Refactor a little bit
        Arrays.stream(declaredMethods)
                .filter(isAnnotatedMethod)
                .findFirst()
                .ifPresent(method -> invokePostConstructMethod(bean, method));
    }

    private void invokePostConstructMethod(Object bean, Method method) {
        try {
            prepareMethod(method).invoke(bean);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            log.error(ERROR_THE_METHOD_THAT_WAS_ANNOTATED_WITH_POST_CONSTRUCT);

            throw new InvalidInvokePostConstructMethodException(
                    ERROR_THE_METHOD_THAT_WAS_ANNOTATED_WITH_POST_CONSTRUCT, exception);
        }
    }

    private Object createBean(BeanDefinition beanDefinition) {
        log.trace("Call createBean({})", beanDefinition);

        try {
            if (beanDefinition instanceof ComponentAnnotationBeanDefinition componentBeanDefinition) {
                return createComponent(componentBeanDefinition);
            } else {
                return createInnerBeanOfConfigClass((BeanAnnotationBeanDefinition) beanDefinition);
            }
        } catch (Exception exception) {
            String message = String.format(ERROR_CREATED_BEAN_OF_TYPE, beanDefinition.getBeanClass().getName());
            log.error(message);

            throw new BeanCreationException(message, exception);
        }
    }

    private Object createInnerBeanOfConfigClass(BeanAnnotationBeanDefinition beanDefinition)
            throws InvocationTargetException, IllegalAccessException {
        var configClassName = beanDefinition.getConfigClassName();
        var configClassBeanDefinition = beanDefinitionFactory.getBeanDefinitionByBeanName(configClassName);
        var configClass = beanMap.get(configClassBeanDefinition.getBeanName());
        if (configClass == null) {
            configClass = saveBean(configClassName, configClassBeanDefinition);
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
            BeanDefinition parameterBeanDefinition = beanDefinitionFactory
                    .getBeanDefinitionByBeanName(resolveBeanName(parameter.getType()));
            Object parameterDependency = beanMap.get(parameterBeanDefinition.getBeanName());
            if (parameterDependency == null) {
                createBean(parameterBeanDefinition);
            }

            args[i] = parameterDependency;
        }

        return args;
    }

    private Object createComponent(ComponentAnnotationBeanDefinition beanDefinition)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        log.trace("Call createComponent({})", beanDefinition);

        Constructor<?> initializationConstructor = prepareConstructor(beanDefinition.getInitializationConstructor());
        Object[] autowireCandidates = retrieveAutowireCandidates(initializationConstructor);

        return initializationConstructor.newInstance(autowireCandidates);
    }

    private Object[] retrieveAutowireCandidates(Constructor<?> initializationConstructor) {
        log.trace("Call retrieveAutowireCandidates({})", initializationConstructor);

        Class<?>[] autowireCandidateTypes = initializationConstructor.getParameterTypes();
        Object[] autowireCandidates = new Object[autowireCandidateTypes.length];
        for (int i = 0; i < autowireCandidateTypes.length; i++) {
            Class<?> autowireCandidateType = autowireCandidateTypes[i];
            autowireCandidates[i] = createBeanIfNotPresent(autowireCandidateType);
        }

        return autowireCandidates;
    }

    public void registerBean(String beanName, BeanDefinition beanDefinition) {
        log.trace("Call registerBean({}, {})", beanName, beanDefinition);
        if (beanDefinition.getScope().equals(ApplicationContext.SCOPE_SINGLETON)) {
            saveBean(beanName, beanDefinition);
        }
    }

    private Object saveBean(String beanName, BeanDefinition beanDefinition) {
        log.trace("Call saveBean({}, {})", beanName, beanDefinition);

        Object bean = createBean(beanDefinition);
        beanMap.put(beanName, bean);
        log.trace("Bean has been saved: {}", bean);

        return bean;
    }

    private void initializeBeanAfterRegistering(String beanName, Object bean) {
        beanMap.put(beanName, initWithBeanPostProcessor(beanName, bean));
    }

    public <T> T getBean(Class<T> requiredType) {
        log.trace("Call getBean({})", requiredType);

        if (!isSelectSingleBeansOfType(requiredType)) {
            return createBeanIfNotPresent(requiredType, true);
        }

        String beanName = BeanNameResolver.resolveBeanName(requiredType);

        return getBean(beanName, requiredType);
    }

    public <T> T getBean(String name, Class<T> requiredType) {
        log.trace("Call getBean({}, {})", name, requiredType);
        Optional<Object> bean = Optional.ofNullable(beanMap.get(name));
        if (bean.isEmpty()) {
            Object createdPrototypeBean = checkAndCreatePrototypeBean(name, requiredType)
                    .orElseThrow(() -> new NoSuchBeanDefinitionException(String
                            .format(NO_BEAN_FOUND_OF_TYPE, requiredType.getName())));
            return requiredType.cast(createdPrototypeBean);
        }

        return requiredType.cast(bean.orElseThrow());
    }

    private <T> T createBeanIfNotPresent(Class<T> requiredType) {
        return createBeanIfNotPresent(requiredType, false);
    }

    private <T> T createBeanIfNotPresent(Class<T> requiredType, boolean onlyPrototype) {
        log.trace("Call createBeanIfNotPresent({}, {})", requiredType, onlyPrototype);

        Map<String, T> beansOfType = getBeansOfType(requiredType);
        if (beansOfType.isEmpty()) {
            BeanDefinition beanDefinitionsOfType = getBeanDefinitionsOfType(requiredType, onlyPrototype);
            Object bean = createBean(beanDefinitionsOfType);

            return requiredType.cast(bean);
        }

        if (beansOfType.size() > 1) {
            return defineSpecificBean(requiredType, beansOfType);
        }

        return beansOfType.values().stream().findAny().orElseThrow();
    }

    private BeanDefinition getBeanDefinitionsOfType(Class<?> requiredType, boolean onlyPrototype) {
        log.trace("Call getBeanDefinitionsOfType({})", requiredType);

        Map<String, BeanDefinition> beanDefinitions = onlyPrototype
                ? beanDefinitionFactory.getPrototypeBeanDefinitionsOfType(requiredType)
                : beanDefinitionFactory.getBeanDefinitionsOfType(requiredType);
        if (beanDefinitions.isEmpty()) {
            String message = String.format(NO_BEAN_DEFINITION_FOUND_OF_TYPE, requiredType.getName());
            log.error(message);

            throw new NoSuchBeanDefinitionException(message);
        }

        if (beanDefinitions.size() == 1) {
            return beanDefinitions.values().stream().toList().get(0);
        }

        List<BeanDefinition> primaryBeanDefinitions = beanDefinitions.values()
                .stream()
                .filter(BeanDefinition::isPrimary)
                .toList();
        if (primaryBeanDefinitions.isEmpty()) {
            String message = String.format(NO_BEAN_DEFINITION_FOUND_OF_TYPE, requiredType.getName());
            log.error(message);

            throw new NoSuchBeanDefinitionException(message);
        }

        if (primaryBeanDefinitions.size() > 1) {
            String message = String
                    .format(ErrorMessageConstants.NO_UNIQUE_BEAN_DEFINITION_FOUND_OF_TYPE, requiredType.getName());
            log.error(message);

            throw new NoUniqueBeanDefinitionException(message);
        }

        return primaryBeanDefinitions.get(0);
    }

    public <T> Map<String, T> getBeansOfType(Class<T> requiredType) {
        return beanMap.entrySet()
                .stream()
                .filter(entry -> requiredType.isAssignableFrom(entry.getValue().getClass()))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> requiredType.cast(entry.getValue())));
    }

    private boolean isSelectSingleBeansOfType(Class<?> requiredType) {
        log.trace("Call isSelectSingleBeansOfType({})", requiredType);

        return beanMap.entrySet()
                .stream()
                .filter(entry -> requiredType.isAssignableFrom(entry.getValue().getClass()))
                .count() == 1;
    }

    private <T> T defineSpecificBean(Class<T> requiredType, Map<String, T> beansOfType) {
        var defineQualifierSpecificBean = defineQualifierSpecificBean(requiredType, beansOfType);

        return defineQualifierSpecificBean.orElseGet(() -> definePrimarySpecificBean(requiredType, beansOfType));
    }

    private <T> T definePrimarySpecificBean(Class<T> requiredType, Map<String, T> beansOfType) {
        log.trace("Call definePrimarySpecificBean({}, {})", requiredType, beansOfType);
        List<T> beansOfRequiredType = beansOfType.values().stream()
                .filter(bean -> beanDefinitionFactory.isBeanPrimary(bean.getClass()))
                .toList();

        if (beansOfRequiredType.isEmpty()) {
            // We have no beans of required type with @Primary
            throw new NoUniqueBeanException(String.format(NO_UNIQUE_BEAN_FOUND_OF_TYPE, requiredType.getName()));
        }

        if (beansOfRequiredType.size() > 1) {
            // We have more than 1 @Primary beans of required type
            throw new NoUniqueBeanException(String.format(NO_UNIQUE_BEAN_FOUND_OF_TYPE, requiredType.getName()));
        }

        return beansOfRequiredType.stream().findAny().orElseThrow();
    }

    private <T> Optional<T> defineQualifierSpecificBean(Class<T> requiredType, Map<String, T> beansOfType) {
        log.trace("Call defineQualifierSpecificBean({}, {})", requiredType, beansOfType);

        if (requiredType.isAnnotationPresent(Qualifier.class)) {
            var qualifier = requiredType.getDeclaredAnnotation(Qualifier.class);

            String beanName = qualifier.value();

            return Optional.ofNullable(beansOfType.get(beanName));
        }

        return Optional.empty();
    }

    public Map<String, Object> getBeans() {
        log.trace("Call getBeans()");

        return beanMap;
    }

    private <T> Optional<T> checkAndCreatePrototypeBean(String name, Class<T> requiredType) {
        Optional<BeanDefinition> beanDefinitionOptional = Optional
                .ofNullable(beanDefinitionFactory.getBeanDefinitionByBeanName(name));
        if (beanDefinitionOptional.isEmpty()) {
            throw new NoSuchBeanDefinitionException(String.format(NO_BEAN_DEFINITION_FOUND_OF_TYPE, requiredType.getName()));
        }

        BeanDefinition beanDefinition = beanDefinitionOptional.orElseThrow();
        if (beanDefinition.getScope().equals(ApplicationContext.SCOPE_PROTOTYPE)) {
            return Optional.of(requiredType.cast(createBean(beanDefinition)));
        }

        return Optional.empty();
    }

}
