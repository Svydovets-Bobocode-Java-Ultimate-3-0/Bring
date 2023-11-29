package svydovets.core.context.beanDefinition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.Bean;
import svydovets.core.annotation.Primary;
import svydovets.core.annotation.Scope;
import svydovets.core.context.beanFactory.BeanFactory;
import svydovets.exception.BeanDefinitionCreateException;
import svydovets.exception.UnsupportedScopeException;
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
import static svydovets.util.BeanNameResolver.resolveBeanName;
import static svydovets.util.ReflectionsUtil.findAutowiredFieldNames;

public class BeanDefinitionFactory {

    private static final Logger log = LoggerFactory.getLogger(BeanDefinitionFactory.class);

    private final Map<String, BeanDefinition> beanDefinitionMap = new LinkedHashMap<>();

    public Map<String, BeanDefinition> registerBeanDefinitions(Set<Class<?>> beanClasses) {
        log.trace("Call registerBeanDefinitions({})", beanClasses);
        for (Class<?> configClass : beanClasses) {
            registerBeanDefinition(configClass);
            beanDefinitionMap.putAll(createBeanDefinitionMapByConfigClass(configClass));
        }
        log.trace("Bean definition map has been created with keys: {}", beanDefinitionMap.keySet());

        return beanDefinitionMap;
    }

    public void registerBeanDefinition(Class<?> beanClass) {
        log.trace("Call registerBeanDefinition({})", beanClass);
        BeanDefinition beanDefinition = createComponentBeanDefinitionByBeanClass(beanClass);
        beanDefinitionMap.put(resolveBeanName(beanClass), beanDefinition);
    }

    public BeanDefinition getBeanDefinitionByBeanName(String beanName) {
        return beanDefinitionMap.get(beanName);
    }

    public Map<String, BeanDefinition> createBeanDefinitionMapByConfigClass(Class<?> configClass) {
        log.trace("Call createBeanDefinitionMapByConfigClass({})", configClass);

        return Arrays.stream(configClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .map(this::createBeanDefinitionByBeanInitMethod)
                .collect(Collectors.toMap(BeanDefinition::getBeanName, Function.identity()));
    }


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


        log.trace("Method base bean definition of class {} has been created: {}", beanInitMethod.getDeclaringClass(), beanDefinition);
        return beanDefinition;
    }

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

    private String resolveScopeName(Method beanInitMethod) {
        log.trace("Call resolveScopeName({})", beanInitMethod);
        Scope scopeAnnotation = beanInitMethod.getAnnotation(Scope.class);
        return doResolveScopeName(scopeAnnotation);
    }

    private String resolveScopeName(Class<?> beanClass) {
        log.trace("Call resolveScopeName({})", beanClass);
        Scope scopeAnnotation = beanClass.getAnnotation(Scope.class);
        return doResolveScopeName(scopeAnnotation);
    }

    private String doResolveScopeName(Scope scopeAnnotation) {
        if (scopeAnnotation != null) {
            String scopeValue = scopeAnnotation.value();
            log.trace("Scope value is specified explicitly: '{}'", scopeValue);
            if (BeanFactory.SUPPORTED_SCOPES.contains(scopeValue)) {
                return scopeValue;
            }
            String errorMessage = String.format(ErrorMessageConstants.UNSUPPORTED_SCOPE_TYPE, scopeValue);
            log.error(errorMessage);

            throw new UnsupportedScopeException(errorMessage);
        }
        log.trace("Scope values is not specified explicitly. Set '{}' by default", SCOPE_SINGLETON);
        return SCOPE_SINGLETON;
    }

    public Map<String, BeanDefinition> getBeanDefinitionsOfType(Class<?> requiredType) {
        return beanDefinitionMap.entrySet()
                .stream()
                .filter(beanDef -> requiredType.isAssignableFrom(beanDef.getValue().getBeanClass()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

        public Map<String, BeanDefinition> getPrototypeBeanDefinitionsOfType(Class<?> requiredType) {
        return beanDefinitionMap.entrySet()
                .stream()
                .filter(beanDef -> isFilteredBeanDefinition(requiredType, beanDef.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private boolean isFilteredBeanDefinition(Class<?> requiredType, BeanDefinition beanDefinition) {
        return requiredType.isAssignableFrom(beanDefinition.getBeanClass())
                && SCOPE_PROTOTYPE.equals(beanDefinition.getScope());
    }

    public boolean isBeanPrimary(Class<?> beanClass) {
        return getBeanDefinitionByBeanName(resolveBeanName(beanClass)).isPrimary();
    }
}
