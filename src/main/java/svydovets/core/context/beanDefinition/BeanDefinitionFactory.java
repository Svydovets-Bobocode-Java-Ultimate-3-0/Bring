package svydovets.core.context.beanDefinition;

import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.Bean;
import svydovets.core.annotation.Primary;
import svydovets.core.annotation.Scope;
import svydovets.core.context.ApplicationContext;
import svydovets.exception.BeanDefinitionCreateException;
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

import static svydovets.util.NameResolver.resolveBeanName;
import static svydovets.util.ReflectionsUtil.findAutowiredFieldNames;

public class BeanDefinitionFactory {

    private final Map<String, BeanDefinition> beanDefinitionMap = new LinkedHashMap<>();

    public Map<String, BeanDefinition> registerBeanDefinitions(Set<Class<?>> beanClasses) {
        for (Class<?> configClass : beanClasses) {
            registerBeanDefinition(configClass);
            beanDefinitionMap.putAll(createBeanDefinitionMapByConfigClass(configClass));
        }
        return beanDefinitionMap;
    }

    public void registerBeanDefinition(Class<?> beanClass) {
        BeanDefinition beanDefinition = createComponentBeanDefinitionByBeanClass(beanClass);
        beanDefinitionMap.put(resolveBeanName(beanClass), beanDefinition);
    }

    public BeanDefinition getBeanDefinitionByBeanName(String beanName) {
        return beanDefinitionMap.get(beanName);
    }
    public Map<String, BeanDefinition> createBeanDefinitionMapByConfigClass(Class<?> configClass) {
        return Arrays.stream(configClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .map(this::createBeanDefinitionByBeanInitMethod)
                .collect(Collectors.toMap(BeanDefinition::getBeanName, Function.identity()));
    }


    public BeanDefinition createComponentBeanDefinitionByBeanClass(Class<?> beanClass) {
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

    public BeanDefinition createBeanDefinitionByBeanInitMethod(Method beanInitMethod) {
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

    private Constructor<?> findInitializationConstructor(Class<?> beanClass) {
        var constructors = Arrays.stream(beanClass.getDeclaredConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(Autowired.class))
                .toList();
        if (constructors.isEmpty()) {
            return ReflectionsUtil.getPreparedNoArgsConstructor(beanClass);
        }
        if (constructors.size() == 1) {
            return Optional.of(constructors.get(0))
                    .map(ReflectionsUtil::prepareConstructor)
                    .orElseThrow();
        }
        throw new BeanDefinitionCreateException(String.format(
                "Error creating bean definition for bean '%s': Invalid autowire-marked constructor: %s. Found constructor with 'required' Autowired annotation already: %s",
                beanClass.getName(),
                constructors.get(1).getName(),
                constructors.get(0).getName())
        );
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

    public Map<String, BeanDefinition> getBeanDefinitionsOfType(Class<?> requiredType) {
        return beanDefinitionMap.entrySet()
                .stream()
                .filter(beanDef -> requiredType.isAssignableFrom(beanDef.getValue().getBeanClass()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public boolean isBeanPrimary(Class<?> beanClass) {
        return getBeanDefinitionByBeanName(resolveBeanName(beanClass)).isPrimary();
    }
}
