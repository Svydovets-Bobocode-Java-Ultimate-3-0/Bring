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
import svydovets.exception.*;
import svydovets.util.ErrorMessageConstants;
import svydovets.util.PackageScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static svydovets.util.NameResolver.resolveBeanName;
import static svydovets.util.ErrorMessageConstants.ERROR_CREATED_BEAN_OF_TYPE;
import static svydovets.util.ErrorMessageConstants.ERROR_NOT_UNIQUE_METHOD_THAT_ANNOTATED_POST_CONSTRUCT;
import static svydovets.util.ErrorMessageConstants.ERROR_THE_METHOD_THAT_WAS_ANNOTATED_WITH_POST_CONSTRUCT;
import static svydovets.util.ErrorMessageConstants.NO_BEAN_DEFINITION_FOUND_OF_TYPE;
import static svydovets.util.ErrorMessageConstants.NO_BEAN_FOUND_OF_TYPE;
import static svydovets.util.ErrorMessageConstants.NO_UNIQUE_BEAN_FOUND_OF_TYPE;
import static svydovets.util.ReflectionsUtil.prepareConstructor;
import static svydovets.util.ReflectionsUtil.prepareMethod;

/**
 * The {@code BeanFactoryImpl} class is an implementation of the {@link BeanFactory} interface,
 * providing comprehensive functionality for managing beans within the custom Inversion of Control (IoC) framework.
 * This class includes features for bean registration, retrieval, lifecycle management, and advanced capabilities
 * such as package scanning and custom command execution.
 *
 * <h2>Supported Scopes:</h2>
 * The {@code BeanFactoryImpl} supports the following bean scopes:
 * <ul>
 *     <li>{@link ApplicationContext#SCOPE_SINGLETON}: Indicates that a single instance of the bean should be created
 *     and shared within the IoC container.</li>
 *     <li>{@link ApplicationContext#SCOPE_PROTOTYPE}: Indicates that a new instance of the bean should be created
 *     whenever it is requested.</li>
 * </ul>
 *
 * <h2>Bean Lifecycle and Post-Processing:</h2>
 * Beans' lifecycle and post-processing are managed through the use of {@link BeanPostProcessor} instances,
 * allowing for customization and processing of beans before and after instantiation. The default implementation
 * includes an {@link AutowiredAnnotationBeanPostProcessor} for handling autowiring dependencies using annotations.
 * Additional post-processors can be added to the {@code beanPostProcessors} list for extended functionality.
 *
 * <h2>Command Factory:</h2>
 * The {@code BeanFactoryImpl} utilizes a {@link CommandFactory} to register and execute commands related to
 * bean creation and retrieval. Commands are registered for operations such as getting a bean by type and
 * getting beans of a specific type. Custom commands can be added to the {@code commandFactory} for specialized use cases.
 *
 * <h2>Package Scanning:</h2>
 * The package scanning functionality is provided by the {@link PackageScanner}, allowing developers to register
 * beans by specifying base packages or base classes. Beans are identified based on specific annotations.
 *
 * <h2>Bean Registration:</h2>
 * The following methods are available for registering beans:
 * <ul>
 *     <li>{@link #registerBeans(String) registerBeans(String basePackage)}: Scans the specified base package for
 *     classes annotated as beans and registers them in the IoC container.</li>
 *     <li>{@link #registerBeans(Class[]) registerBeans(Class<?>... classes)}: Manually registers the provided classes
 *     as beans in the IoC container.</li>
 *     <li>{@link #registerBean(String, BeanDefinition) registerBean(String beanName, BeanDefinition beanDefinition)}:
 *     Manually registers a bean with a specified name and its corresponding {@link BeanDefinition}.</li>
 * </ul>
 *
 * <h2>Bean Retrieval:</h2>
 * The following methods are available for retrieving beans:
 * <ul>
 *     <li>{@link #getBean(Class) getBean(Class<T> requiredType)}: Retrieves a bean of the specified type from the IoC container.</li>
 *     <li>{@link #getBean(String, Class) getBean(String name, Class<T> requiredType)}: Retrieves a named bean of the specified type
 *     from the IoC container.</li>
 *     <li>{@link #getBeansOfType(Class) getBeansOfType(Class<T> requiredType)}: Retrieves all beans of the specified type from the
 *     IoC container, mapping bean names to their instances.</li>
 *     <li>{@link #getBeans() getBeans()}: Retrieves all registered beans in the IoC container, mapping bean names to their instances.</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 *  * <pre>
 *  * // Create an instance of the BeanFactory
 *  * BeanFactory beanFactory = new BeanFactoryImpl();
 *  *
 *  * // Register beans by scanning a base package
 *  * beanFactory.registerBeans("com.example.beans");
 *  *
 *  * // Register beans manually
 *  * BeanDefinition beanDefinition = new ComponentAnnotationBeanDefinition("myBean", MyBean.class)
 *  * beanFactory.registerBean("myBean", beanDefinition);
 *  *
 *  * // Retrieve a bean by type
 *  * MyBean myBean = beanFactory.getBean(MyBean.class);
 *  *
 *  * // Retrieve a named bean by type
 *  * AnotherBean anotherBean = beanFactory.getBean("anotherBean", AnotherBean.class);
 *  *
 *  * // Retrieve all beans of a specific type
 *  * Map<String, SomeInterface> beansOfType = beanFactory.getBeansOfType(SomeInterface.class);
 *  *
 *  * // Retrieve all registered beans
 *  * Map<String, Object> allBeans = beanFactory.getBeans();
 *  * </pre>
 *
 * <p>
 * <b>Note:</b> Developers should be familiar with IoC principles and specific annotations or configurations required
 * for beans to be correctly identified and registered within the IoC container. Additionally, customizations such as
 * post-processors and commands can be added to extend the functionality of the {@code BeanFactoryImpl}.
 *
 * @see PackageScanner
 * @see BeanPostProcessor
 * @see BeanDefinitionFactory
 * @see CommandFactory
 */
public class BeanFactoryImpl implements BeanFactory {
    private static final Logger log = LoggerFactory.getLogger(BeanFactoryImpl.class);

    public static final Set<String> SUPPORTED_SCOPES = new HashSet<>(Arrays.asList(
            ApplicationContext.SCOPE_SINGLETON,
            ApplicationContext.SCOPE_PROTOTYPE
    ));


    private final Map<String, Object> beanMap = new LinkedHashMap<>();

    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    private final PackageScanner packageScanner = new PackageScanner();

    private final BeanDefinitionFactory beanDefinitionFactory = new BeanDefinitionFactory();

    private final CommandFactory commandFactory = new CommandFactory();

    public BeanFactoryImpl() {
        commandFactory.registryCommand(CommandFunctionName.FC_GET_BEAN, this::createBeanIfNotPresent);
        commandFactory.registryCommand(CommandFunctionName.FC_GET_BEANS_OF_TYPE, this::getBeansOfType);
        beanPostProcessors.add(new AutowiredAnnotationBeanPostProcessor(commandFactory));
    }

    /**
     * Scans the specified base package for classes annotated as beans and registers them in the bean map.
     *
     * @param basePackage The base package to scan for classes annotated as beans.
     */
    @Override
    public void registerBeans(String basePackage) {
        log.info("Scanning package: {}", basePackage);
        Set<Class<?>> beanClasses = packageScanner.findComponentsByBasePackage(basePackage);  //TODO why dont search Components + Configurations ? (like below)
        log.info("Registering beans");
        doRegisterBeans(beanClasses);
    }

    @Override
    public void registerBeans(Class<?>... classes) {
        Set<Class<?>> beanClasses = packageScanner.findAllBeanCandidatesByBaseClass(classes);
        doRegisterBeans(beanClasses);
    }

    @Override
    public void registerBean(String beanName, BeanDefinition beanDefinition) {
        log.trace("Call registerBean({}, {})", beanName, beanDefinition);
        if (beanDefinition.getScope().equals(ApplicationContext.SCOPE_SINGLETON)
                && beanDefinition.getCreationStatus().equals(BeanDefinition.BeanCreationStatus.NOT_CREATED.name())) {
            saveBean(beanName, beanDefinition);
        }
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        log.trace("Call getBean({})", requiredType);

        if (!isSelectSingleBeansOfType(requiredType) || isSelectMoreOneBeanDefinitionsOfType(requiredType)) {
            return createBeanIfNotPresent(requiredType, true);
        }

        String beanName = resolveBeanName(requiredType);

        return getBean(beanName, requiredType);
    }

    @Override
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

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> requiredType) {
        return beanMap.entrySet()
                .stream()
                .filter(entry -> requiredType.isAssignableFrom(entry.getValue().getClass()))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> requiredType.cast(entry.getValue())));
    }

    @Override
    public Map<String, Object> getBeans() {
        log.trace("Call getBeans()");

        return beanMap;
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

        beanDefinition.setCreationStatus(BeanDefinition.BeanCreationStatus.IN_PROGRESS);
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
            configClass = createBeanBasedOnItScope(configClassName, configClassBeanDefinition);
        }

        var initMethod = beanDefinition.getInitMethodOfBeanFromConfigClass();
        Object[] args = retrieveBeanInitMethodArguments(initMethod);

        // TODO: Можна винести в окремий метод, щоб опрацювати помилки InvocationTargetException, IllegalAccessException
        return prepareMethod(initMethod).invoke(configClass, args);
    }

    private Object[] retrieveBeanInitMethodArguments(Method initMethod) {
        Parameter[] parameters = initMethod.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            String beanName = resolveBeanName(parameter.getType());
            BeanDefinition beanDefinition = beanDefinitionFactory
                    .getBeanDefinitionByBeanName(beanName);
            Object parameterDependency = beanMap.get(beanName);
            if (parameterDependency == null) {
                checkIfCircularDependencyExist(beanDefinition);
                parameterDependency = createBeanBasedOnItScope(beanName, beanDefinition);
            }
            args[i] = parameterDependency;
        }

        return args;
    }

    private Object createBeanBasedOnItScope(String beanName, BeanDefinition beanDefinition) {
        return ApplicationContext.SCOPE_SINGLETON.equals(beanDefinition.getScope())
                ? saveBean(beanName, beanDefinition)
                : createBean(beanDefinition);
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

    private <T> T createBeanIfNotPresent(Class<T> requiredType) {
        return createBeanIfNotPresent(requiredType, false);
    }

    private <T> T createBeanIfNotPresent(Class<T> requiredType, boolean onlyPrototype) {
        log.trace("Call createBeanIfNotPresent({}, {})", requiredType, onlyPrototype);

        Map<String, T> beansOfType = getBeansOfType(requiredType);
        if (beansOfType.isEmpty()) {
            BeanDefinition beanDefinitionOfType = getBeanDefinitionsOfType(requiredType, onlyPrototype);
            checkIfCircularDependencyExist(beanDefinitionOfType);

            String beanName = beanDefinitionOfType.getBeanName();
            Object bean = createBeanBasedOnItScope(beanName, beanDefinitionOfType);

            return requiredType.cast(bean);
        }

        if (beansOfType.size() > 1) {
            return defineSpecificBean(requiredType, beansOfType);
        }

        return beansOfType.values().stream().findAny().orElseThrow();
    }

    private void checkIfCircularDependencyExist(BeanDefinition beanDefinition) {
        if (beanDefinition.getScope().equals(ApplicationContext.SCOPE_SINGLETON)
                && BeanDefinition.BeanCreationStatus.IN_PROGRESS.name().equals(beanDefinition.getCreationStatus())) {
            throw new UnresolvedCircularDependencyException(String
                    .format(ErrorMessageConstants.CIRCULAR_DEPENDENCY_DETECTED, beanDefinition.getBeanClass().getName())
            );
        }
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

    private boolean isSelectSingleBeansOfType(Class<?> requiredType) {
        log.trace("Call isSelectSingleBeansOfType({})", requiredType);

        return beanMap.entrySet()
                .stream()
                .filter(entry -> requiredType.isAssignableFrom(entry.getValue().getClass()))
                .count() == 1;
    }

    private boolean isSelectMoreOneBeanDefinitionsOfType(Class<?> requiredType) {
        log.trace("Call isSelectMoreOneBeanDefinitionsOfType({})", requiredType);

        return beanDefinitionFactory.getBeanDefinitionsOfType(requiredType).entrySet()
                .stream()
                .filter(entry -> requiredType.isAssignableFrom(entry.getValue().getBeanClass()))
                .count() > 1;
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

    public BeanDefinitionFactory beanDefinitionFactory() {
        return beanDefinitionFactory;
    }
}
