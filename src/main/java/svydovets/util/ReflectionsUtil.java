package svydovets.util;

import svydovets.core.context.beanDefenition.BeanDefinition;
import svydovets.core.context.beanDefenition.DefaultBeanDefinition;


public class ReflectionsUtil {

    private BeanDefinition createBeanDefinitionByBeanClass(Class<?> classType) {
        var beanDefinitionBuilder = DefaultBeanDefinition.builder().beanClass(classType);
        
        return beanDefinitionBuilder.build();
    }

}
