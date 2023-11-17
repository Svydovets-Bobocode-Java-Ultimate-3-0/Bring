package svydovets.core.bpp;

import svydovets.core.annotation.Autowired;
import svydovets.core.context.beanFactory.CommandBeanFactory;
import svydovets.core.context.injector.InjectorConfig;
import svydovets.core.context.injector.InjectorExecutor;
import svydovets.exception.AutowireBeanException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

// todo: Maybe it is better to add new annotation @BeanPostProcessor
public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor {

    private CommandBeanFactory commandBeanPostProcessor;

    public AutowiredAnnotationBeanPostProcessor() {
    }

    public AutowiredAnnotationBeanPostProcessor(CommandBeanFactory commandBeanPostProcessor) {
        this.commandBeanPostProcessor = commandBeanPostProcessor;
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

    public void populateProperties(Object bean) {
        doSetterInjection(bean);
        doFieldInjection(bean);
    }

    private void doSetterInjection(Object bean) {
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
        Field[] beanFields = bean.getClass().getDeclaredFields();
        for (Field beanField : beanFields) {
            boolean isAutowiredPresent = beanField.isAnnotationPresent(Autowired.class);

            if (isAutowiredPresent) {
                InjectorConfig injectorConfig = InjectorConfig.builder()
                        .withBean(bean)
                        .withBeanField(beanField)
                        .withBeanReceiver(commandBeanPostProcessor.getBeanCommand())
                        .withBeanOfTypeReceiver(commandBeanPostProcessor.getBeansOfTypeCommand())
                        .build();

                InjectorExecutor.execute(injectorConfig);
            }
        }
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

    private Stream<Object> getBeanForSetterMethod(Class<?>[] parameterTypes) {
        return Arrays.stream(parameterTypes)
                .map(commandBeanPostProcessor.getBeanCommand());
    }

}
