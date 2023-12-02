package svydovets.core.bpp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.Qualifier;
import svydovets.core.context.beanFactory.command.CommandFactory;
import svydovets.core.context.beanFactory.command.CommandFunctionName;
import svydovets.core.context.injector.InjectorConfig;
import svydovets.core.context.injector.InjectorExecutor;
import svydovets.core.exception.AutowireBeanException;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * The {@code AutowiredAnnotationBeanPostProcessor} is an implementation of the {@link BeanPostProcessor}
 * interface. It is responsible for post-processing beans to perform autowiring,
 * specifically handling injection of dependencies marked with the {@link Autowired} annotation.
 *
 * <p>This bean post-processor supports setter and field injection mechanisms for autowiring. It is
 * configured with a {@link CommandFactory} that allows dynamic retrieval of beans based on certain
 * criteria, such as bean type and qualifier annotations.
 *
 * <p>The typical use case for this post-processor is to automatically inject dependencies into beans,
 * reducing the need for explicit configuration and promoting a more declarative style of dependency
 * injection.
 *
 * <p>The autowiring process is triggered during the bean initialization phase, specifically in the
 * {@link #postProcessBeforeInitialization(Object, String)} method. The actual injection is performed
 * based on the presence of the {@code Autowired} annotation on setter methods and fields.
 *
 * @see CommandFactory
 * @see InjectorConfig
 * @see InjectorExecutor
 */
public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(AutowiredAnnotationBeanPostProcessor.class);

    private final CommandFactory commandFactory;

    /**
     * Constructs an {@code AutowiredAnnotationBeanPostProcessor} with the specified {@link CommandFactory}.
     *
     * @param commandFactory the factory for executing commands to retrieve beans dynamically
     */
    public AutowiredAnnotationBeanPostProcessor(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    /**
     * <p>Performs autowiring by populating properties (setter and field injection) before bean initialization</p>.
     *
     * @param bean     the bean instance being processed
     * @param beanName the name of the bean
     * @return the potentially modified bean instance
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        populateProperties(bean);
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    /**
     * <p>No additional processing is performed after bean initialization.
     *
     * @param bean     the fully initialized bean instance
     * @param beanName the name of the bean
     * @return the potentially modified bean instance
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    /**
     * Populates properties of the specified bean by performing both setter and field injection.
     *
     * @param bean the bean instance to be processed
     */
    private void populateProperties(Object bean) {
        log.trace("Call populateProperties({})", bean);

        doSetterInjection(bean);
        doFieldInjection(bean);
    }

    /**
     * Performs setter injection by identifying methods annotated with {@code Autowired} and invoking them
     * to inject the corresponding dependencies.
     *
     * @param bean the bean instance to be processed
     */
    private void doSetterInjection(Object bean) {
        log.trace("Call doSetterInjection({})", bean);
        Method[] declaredMethods = bean.getClass().getDeclaredMethods();

        List<Method> targetMethods = Arrays.stream(declaredMethods)
                .filter(method -> method.isAnnotationPresent(Autowired.class))
                .toList();

        Object[] injectBeans = targetMethods.stream()
                .map(Executable::getParameters)
                .flatMap(this::getParameterTypes)
                .flatMap(this::getBeanForSetterMethod)
                .toArray();

        invokeSetterMethod(targetMethods, bean, injectBeans);
    }

    /**
     * Performs field injection by identifying fields annotated with {@code Autowired} and using the
     * configured {@link InjectorExecutor} to inject the corresponding dependencies.
     *
     * @param bean the bean instance to be processed
     */
    private void doFieldInjection(Object bean) {
        if (log.isTraceEnabled()) {
            log.trace("Call doFieldInjection({})", bean);
        }

        Field[] beanFields = bean.getClass().getDeclaredFields();
        for (Field beanField : beanFields) {
            boolean isAutowiredPresent = beanField.isAnnotationPresent(Autowired.class);

            if (isAutowiredPresent) {
                InjectorConfig injectorConfig = InjectorConfig.builder()
                        .withBean(bean)
                        .withBeanField(beanField)
                        .withBeanReceiver(clazz -> commandFactory.execute(CommandFunctionName.FC_GET_BEAN).apply(clazz))
                        .withBeanOfTypeReceiver(clazz -> (Map<String, ?>) commandFactory.execute(CommandFunctionName.FC_GET_BEANS_OF_TYPE).apply(clazz))
                        .build();
                InjectorExecutor.execute(injectorConfig);
            }
        }
    }

    /**
     * Invokes setter methods with the specified injectable beans.
     *
     * @param methods     the list of setter methods to be invoked
     * @param targetBean  the target bean instance
     * @param injectBeans the array of injectable beans
     */
    private static void invokeSetterMethod(List<Method> methods, Object targetBean, Object[] injectBeans) {
        log.trace("Call invokeSetterMethod({}, {}, {})", methods, targetBean, injectBeans);

        try {
            for (Method method : methods) {
                method.setAccessible(true);
                method.invoke(targetBean, injectBeans);
            }
        } catch (IllegalAccessException | InvocationTargetException exception) {
            log.error("Error accessing or invoking setter method.", exception);

            throw new AutowireBeanException("Error accessing or invoking setter method.", exception);
        }
    }

    /**
     * Retrieves parameter types from an array of parameters.
     *
     * @param parameters the array of parameters
     * @return a stream of parameter types
     */
    private Stream<Class<?>> getParameterTypes(Parameter[] parameters) {
        return Arrays.stream(parameters)
                .map(this::defineSpecificTypeFromParameter);
    }

    /**
     * Defines the specific type from a parameter, considering the presence of the {@link Qualifier} annotation.
     *
     * @param parameter the parameter for which the type is defined
     * @return the defined parameter type
     */
    private Class<?> defineSpecificTypeFromParameter(Parameter parameter) {
        if (parameter.isAnnotationPresent(Qualifier.class)) {
            var qualifier = parameter.getDeclaredAnnotation(Qualifier.class);

            String beanName = qualifier.value();

            Map<String, ?> beans = (Map<String, ?>) commandFactory.execute(CommandFunctionName.FC_GET_BEANS_OF_TYPE)
                    .apply(parameter.getType());
            Optional<?> foundBean = Optional.ofNullable(beans.get(beanName));

            return foundBean.orElseThrow().getClass();
        }

        return parameter.getType();
    }

    /**
     * Retrieves injectable beans for a setter method based on the specified parameter type.
     *
     * @param parameterType the type of the parameter for which the bean is retrieved
     * @return a stream of injectable beans
     */
    private Stream<Object> getBeanForSetterMethod(Class<?> parameterType) {
        return Stream.of(commandFactory.execute(CommandFunctionName.FC_GET_BEAN).apply(parameterType));
    }
}

