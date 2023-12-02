package svydovets.core.context.beanDefinition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.Bean;
import svydovets.core.annotation.Primary;
import svydovets.core.annotation.Scope;
import svydovets.core.context.beanFactory.BeanFactoryImpl;
import svydovets.core.exception.BeanDefinitionCreateException;
import svydovets.core.exception.UnsupportedScopeException;
import svydovets.util.ErrorMessageConstants;
import svydovets.util.ReflectionsUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static svydovets.core.context.ApplicationContext.SCOPE_PROTOTYPE;
import static svydovets.core.context.ApplicationContext.SCOPE_SINGLETON;
import static svydovets.util.NameResolver.resolveBeanName;
import static svydovets.util.ReflectionsUtil.findAutowiredFieldNames;

/**
 * The BeanDefinitionFactory class manages the creation, registration, and retrieval of
 * BeanDefinitions in a Spring-like framework context.
 */
public class BeanDefinitionFactory {

    private static final Logger log = LoggerFactory.getLogger(BeanDefinitionFactory.class);

    private final Map<String, BeanDefinition> beanDefinitionMap = new LinkedHashMap<>();

    /**
     * Registers bean definitions for the provided set of bean classes.
     *
     * @param beanClasses The set of classes representing beans to register.
     * @return A map containing registered bean definitions.
     */
    public Map<String, BeanDefinition> registerBeanDefinitions(Set<Class<?>> beanClasses) {
        log.trace("Call registerBeanDefinitions({})", beanClasses);
        for (Class<?> configClass : beanClasses) {
            registerBeanDefinition(configClass);
            beanDefinitionMap.putAll(createBeanDefinitionMapByConfigClass(configClass));
        }

        log.trace("Bean definition map has been created with keys: {}", beanDefinitionMap.keySet());

        return beanDefinitionMap;
    }

    /**
     * Registers a bean definition based on a given bean class.
     *
     * @param beanClass The class representing the bean to register.
     */
    public void registerBeanDefinition(Class<?> beanClass) {
        log.trace("Call registerBeanDefinition({})", beanClass);
        BeanDefinition beanDefinition = createComponentBeanDefinitionByBeanClass(beanClass);
        beanDefinitionMap.put(resolveBeanName(beanClass), beanDefinition);
    }

    /**
     * Retrieves a bean definition by its bean name.
     *
     * @param beanName The name of the bean.
     * @return The BeanDefinition associated with the given bean name.
     */
    public BeanDefinition getBeanDefinitionByBeanName(String beanName) {
        return beanDefinitionMap.get(beanName);
    }

    /**
     * Retrieves bean definitions of a specific type.
     *
     * @param "requiredType" The required type of bean definitions.
     * @return A map containing bean definitions of the specified type.
     */
    public Map<String, BeanDefinition> createBeanDefinitionMapByConfigClass(Class<?> configClass) {
        log.trace("Call createBeanDefinitionMapByConfigClass({})", configClass);

        return Arrays.stream(configClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .map(this::createBeanDefinitionByBeanInitMethod)
                .collect(Collectors.toMap(BeanDefinition::getBeanName, Function.identity()));
    }

    /**
     * Creates a component-based BeanDefinition using a specified bean class.
     *
     * @param beanClass The class representing the component-based bean.
     * @return The created BeanDefinition associated with the specified bean class.
     */
    public BeanDefinition createComponentBeanDefinitionByBeanClass(Class<?> beanClass) {
        log.trace("Call createComponentBeanDefinitionByBeanClass({})", beanClass);
        ComponentAnnotationBeanDefinition beanDefinition = new ComponentAnnotationBeanDefinition(
                resolveBeanName(beanClass),
                beanClass
        );
        beanDefinition.setInitializationConstructor(findInitializationConstructor(beanClass));
        beanDefinition.setAutowiredFieldNames(findAutowiredFieldNames(beanClass));
        beanDefinition.setPrimary(beanClass.isAnnotationPresent(Primary.class));
        beanDefinition.setScope(resolveScopeName(beanClass));
        beanDefinition.setCreationStatus(BeanDefinition.BeanCreationStatus.NOT_CREATED);

        log.trace("Bean definition of class {} has been created: {}", beanClass, beanDefinition);

        return beanDefinition;
    }

    /**
     * Creates a BeanDefinition using a specified bean initialization method.
     *
     * @param beanInitMethod The method representing the initialization method for the bean.
     * @return The created BeanDefinition associated with the specified bean initialization method.
     */
    public BeanDefinition createBeanDefinitionByBeanInitMethod(Method beanInitMethod) {
        log.trace("Call createBeanDefinitionByBeanInitMethod({})", beanInitMethod);
        BeanAnnotationBeanDefinition beanDefinition = new BeanAnnotationBeanDefinition(
                resolveBeanName(beanInitMethod),
                beanInitMethod.getReturnType()
        );

        beanDefinition.setScope(resolveScopeName(beanInitMethod));
        beanDefinition.setPrimary(beanInitMethod.isAnnotationPresent(Primary.class));
        beanDefinition.setInitMethodOfBeanFromConfigClass(beanInitMethod);
        beanDefinition.setConfigClassName(resolveBeanName(beanInitMethod.getDeclaringClass()));
        beanDefinition.setCreationStatus(BeanDefinition.BeanCreationStatus.NOT_CREATED);


        log.trace("Method base bean definition of class {} has been created: {}",
                beanInitMethod.getDeclaringClass(), beanDefinition);

        return beanDefinition;
    }

    /**
     * Finds and retrieves the initialization constructor for a given bean class.
     *
     * @param beanClass The class representing the bean for which the initialization constructor is to be found.
     * @return The initialization constructor for the specified bean class, which may be annotated with @Autowired.
     * @throws BeanDefinitionCreateException if an error occurs due to an invalid number of constructors or their configurations.
     */
    private Constructor<?> findInitializationConstructor(Class<?> beanClass) {
        log.trace("Call findInitializationConstructor({})", beanClass);
        var constructors = Arrays.stream(beanClass.getDeclaredConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(Autowired.class))
                .toList();
        if (constructors.isEmpty()) {
            log.trace("Constructor for autowiring is not specified explicitly. Try to get default constructor for {}", beanClass);

            return ReflectionsUtil.getPreparedNoArgsConstructor(beanClass);
        }
        if (constructors.size() == 1) {
            log.trace("Constructor for autowiring is specified explicitly for {}", beanClass);

            return Optional.of(constructors.get(0))
                    .map(ReflectionsUtil::prepareConstructor)
                    .orElseThrow();
        }
        String errorMessage = String.format(
                ErrorMessageConstants.ERROR_CREATING_BEAN_DEFINITION_FOR_BEAN_WITH_INVALID_CONSTRUCTORS,
                beanClass.getName(),
                constructors.get(1).getName(),
                constructors.get(0).getName());
        log.error(errorMessage);

        throw new BeanDefinitionCreateException(errorMessage);
    }

    /**
     * Resolves the scope name based on a given bean initialization method.
     *
     * @param beanInitMethod The method representing the initialization method for the bean.
     * @return The resolved scope name for the bean.
     */
    private String resolveScopeName(Method beanInitMethod) {
        log.trace("Call resolveScopeName({})", beanInitMethod);
        Scope scopeAnnotation = beanInitMethod.getAnnotation(Scope.class);

        return doResolveScopeName(scopeAnnotation);
    }

    /**
     * Resolves the scope name based on a given bean class.
     *
     * @param beanClass The class representing the bean.
     * @return The resolved scope name for the bean.
     */
    private String resolveScopeName(Class<?> beanClass) {
        log.trace("Call resolveScopeName({})", beanClass);
        Scope scopeAnnotation = beanClass.getAnnotation(Scope.class);

        return doResolveScopeName(scopeAnnotation);
    }

    /**
     * Resolves the scope name based on the provided scope annotation.
     *
     * @param scopeAnnotation The Scope annotation associated with the bean.
     * @return The resolved scope name for the bean.
     * @throws UnsupportedScopeException if the specified scope is not supported according to predefined scopes.
     */
    private String doResolveScopeName(Scope scopeAnnotation) {
        if (scopeAnnotation != null) {
            String scopeValue = scopeAnnotation.value();
            log.trace("Scope value is specified explicitly: '{}'", scopeValue);
            if (BeanFactoryImpl.SUPPORTED_SCOPES.contains(scopeValue)) {
                return scopeValue;
            }

            String errorMessage = String.format(ErrorMessageConstants.UNSUPPORTED_SCOPE_TYPE, scopeValue);
            log.error(errorMessage);

            throw new UnsupportedScopeException(errorMessage);
        }
        log.trace("Scope values is not specified explicitly. Set '{}' by default", SCOPE_SINGLETON);
        return SCOPE_SINGLETON;
    }

    /**
     * Retrieves bean definitions of a specific type from the factory's bean definition map.
     *
     * @param requiredType The required type of bean definitions.
     * @return A map containing bean definitions of the specified type.
     */
    public Map<String, BeanDefinition> getBeanDefinitionsOfType(Class<?> requiredType) {
        return beanDefinitionMap.entrySet()
                .stream()
                .filter(beanDef -> requiredType.isAssignableFrom(beanDef.getValue().getBeanClass()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Retrieves prototype bean definitions of a specific type from the factory's bean definition map.
     *
     * @param requiredType The required type of bean definitions.
     * @return A map containing prototype bean definitions of the specified type.
     */
    public Map<String, BeanDefinition> getPrototypeBeanDefinitionsOfType(Class<?> requiredType) {
        return beanDefinitionMap.entrySet()
                .stream()
                .filter(beanDef -> isFilteredBeanDefinition(requiredType, beanDef.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Checks if a bean definition, based on the required type, is a filtered bean definition with a specific scope.
     *
     * @param requiredType The required type of bean definitions.
     * @param beanDefinition The bean definition to check.
     * @return true if the bean definition matches the required type and has a prototype scope, otherwise false.
     */
    private boolean isFilteredBeanDefinition(Class<?> requiredType, BeanDefinition beanDefinition) {
        return requiredType.isAssignableFrom(beanDefinition.getBeanClass())
                && SCOPE_PROTOTYPE.equals(beanDefinition.getScope());
    }

    /**
     * Checks if a bean of a given class is marked as primary.
     *
     * @param beanClass The class representing the bean.
     * @return true if the bean is marked as primary, otherwise false.
     */
    public boolean isBeanPrimary(Class<?> beanClass) {
        return getBeanDefinitionByBeanName(resolveBeanName(beanClass)).isPrimary();
    }
}
