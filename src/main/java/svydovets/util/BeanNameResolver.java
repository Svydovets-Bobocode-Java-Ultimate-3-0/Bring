package svydovets.util;

import java.lang.reflect.Method;

public class BeanNameResolver {
    public static String resolveBeanNameByBeanType(Class<?> beanClass) {
        return beanClass.getSimpleName();
    }

    public static String resolveBeanNameByBeanInitMethod(Method initMethod) {
        return initMethod.getName();
    }
}
