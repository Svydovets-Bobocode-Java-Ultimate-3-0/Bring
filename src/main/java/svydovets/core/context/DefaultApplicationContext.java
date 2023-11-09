package svydovets.core.context;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.Component;
import svydovets.exception.BeanCreationException;
import svydovets.exception.NoDefaultConstructor;
import svydovets.exception.NoSuchBeanException;
import svydovets.exception.NoUniqueBeanException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultApplicationContext implements ApplicationContext {
    private final Map<String, Object> beanMap = new HashMap<>();

    public DefaultApplicationContext(String basePackage) {
        // todo: Rewrite using 'Scanner' class
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages(basePackage)
                .setScanners(Scanners.TypesAnnotated)
        );
        Set<Class<?>> beanTypes = reflections.getTypesAnnotatedWith(Component.class);
        registerBeans(beanTypes);
        populateProperties();
    }

    public DefaultApplicationContext(Class<?>... componentClasses) {
        // todo: Implement the logic of creating context with passed "config" class
        // todo: case 1:
        //  ApplicationContext context = new DefaultApplicationContext(MessageService.class, EditService.class) ->
        //  Create an application context based on 2 passed classes
        // todo: case 2:
        //  ApplicationContext context = new DefaultApplicationContext(BeanConfig.class) ->
        //  BeanConfig - it is a class marked by @Configuration and @ComponentScan annotations
        //  Create an application context based on specified package in @ComponentScan + instance of BeanConfig + @Bean annotation
        // todo: case 3 (case 1 + case 2):
        //  ApplicationContext context = new DefaultApplicationContext(BeanConfig.class, MessageService.class, EditService.class)
        //  Create an application context based on "config" class + additional specified classes that may not be found by the @ComponentScan,
        //  because located in another package
    }

    private void registerBeans(Set<Class<?>> beanTypes) {
        for (var beanType : beanTypes) {
            Object bean = createBean(beanType);
            String beanName = beanType.getSimpleName();
            beanMap.putIfAbsent(beanName, bean);
        }
    }

    private Object createBean(Class<?> beanType) {
        Constructor<?> noArgsConstructor = getPreparedNoArgsConstructor(beanType);
        try {
            return noArgsConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BeanCreationException(String.format("Error creating bean of type %s", beanType.getSimpleName()), e);
        }
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

    private void populateProperties(){
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            Object object = entry.getValue();
            Field[] fields = object.getClass().getDeclaredFields();

            for (Field field : fields) {
                boolean annotationPresent = field.isAnnotationPresent(Autowired.class);

                if (annotationPresent) {
                    Object autowireCandidate = getBean(field.getType());
                    try {
                        field.setAccessible(true);
                        field.set(object, autowireCandidate);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
