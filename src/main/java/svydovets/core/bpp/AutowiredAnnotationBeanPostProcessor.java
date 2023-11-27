package svydovets.core.bpp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svydovets.core.annotation.Autowired;
import svydovets.core.context.beanFactory.command.CommandFactory;
import svydovets.core.context.beanFactory.command.CommandFunctionName;
import svydovets.core.context.injector.BeanInjector;
import svydovets.core.context.injector.InjectorConfig;
import svydovets.core.context.injector.InjectorExecutor;
import svydovets.exception.AutowireBeanException;
import svydovets.util.ErrorMessageConstants;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static svydovets.util.ErrorMessageConstants.ERROR_NO_ACCESS_TO_METHOD;

public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(AutowiredAnnotationBeanPostProcessor.class);

    private CommandFactory commandFactory;

    public AutowiredAnnotationBeanPostProcessor() {
        //empty constructor
    }

    public AutowiredAnnotationBeanPostProcessor(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        populateProperties(bean);
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    private void populateProperties(Object bean) {
        log.trace("Call populateProperties({})", bean);

        doSetterInjection(bean);
        doFieldInjection(bean);
    }

    private void doSetterInjection(Object bean) {
        log.trace("Call doSetterInjection({})", bean);
        Method[] declaredMethods = bean.getClass().getDeclaredMethods();

        List<Method> targetMethod = Arrays.stream(declaredMethods)
                .filter(method -> method.isAnnotationPresent(Autowired.class))
                .toList();

        Object[] injectBeans = targetMethod.stream()
                .map(Method::getParameterTypes)
                .flatMap(this::getBeanForSetterMethod)
                .toArray();

        invokeSetterMethod(targetMethod, bean, injectBeans);
    }

    private void doFieldInjection(Object bean) {
        log.trace("Call doFieldInjection({})", bean);

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

    private static void invokeSetterMethod(List<Method> methods, Object targetBean, Object[] injectBeans) {
        log.trace("Call invokeSetterMethod({}, {}, {})", methods, targetBean, injectBeans);

        try {
            for (Method method : methods) {
                method.setAccessible(true);
                method.invoke(targetBean, injectBeans);
            }
        } catch (IllegalAccessException | InvocationTargetException exception) {
            log.error(ERROR_NO_ACCESS_TO_METHOD);

            throw new AutowireBeanException(ERROR_NO_ACCESS_TO_METHOD);
        }
    }

    private Stream<Object> getBeanForSetterMethod(Class<?>[] parameterTypes) {
        return Arrays.stream(parameterTypes)
                .map(clazz -> commandFactory.execute(CommandFunctionName.FC_GET_BEAN).apply(clazz));
    }

}
