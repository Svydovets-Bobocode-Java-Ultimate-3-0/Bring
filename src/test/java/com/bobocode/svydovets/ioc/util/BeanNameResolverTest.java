package com.bobocode.svydovets.ioc.util;

import com.bobocode.svydovets.source.base.CommonService;
import com.bobocode.svydovets.source.base.MessageService;
import com.bobocode.svydovets.source.config.BasePackageWithAdditionalBeansConfig;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import svydovets.core.annotation.Bean;
import svydovets.core.annotation.Component;
import svydovets.util.BeanNameResolver;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BeanNameResolverTest {

    @Test
    public void shouldReturnComponentAnnotationValueIfNameIsSpecifiedExplicitly() {
        String beanName = MessageService.class.getAnnotation(Component.class).value();
        assertThat(beanName).isEqualTo(BeanNameResolver.resolveBeanName(MessageService.class));
    }

    @Test
    public void shouldReturnSimpleClassNameIfComponentAnnotationValueIsNotSpecifiedExplicitly() {
        String beanName = "commonService";
        assertThat(beanName).isEqualTo(BeanNameResolver.resolveBeanName(CommonService.class));
    }

    @Test
    public void shouldReturnBeanAnnotationValueIfNameIsSpecifiedExplicitly() throws NoSuchMethodException {
        Class<BasePackageWithAdditionalBeansConfig> configClass = BasePackageWithAdditionalBeansConfig.class;
        var trimServiceMethod = configClass.getDeclaredMethod("trimService");
        var beanName = trimServiceMethod.getAnnotation(Bean.class).value();
        assertThat(beanName).isEqualTo(BeanNameResolver.resolveBeanName(trimServiceMethod));
    }

    @Test
    public void shouldReturnMethodNameIfBeanAnnotationValueIsNotSpecifiedExplicitly() throws NoSuchMethodException {
        Class<BasePackageWithAdditionalBeansConfig> configClass = BasePackageWithAdditionalBeansConfig.class;
        var messageServiceMethod = configClass.getDeclaredMethod("orderService");
        var beanName = messageServiceMethod.getName();
        assertThat(beanName).isEqualTo(BeanNameResolver.resolveBeanName(messageServiceMethod));
    }
}
