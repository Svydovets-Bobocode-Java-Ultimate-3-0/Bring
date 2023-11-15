package com.bobocode.svydovets.util;

import com.bobocode.svydovets.config.BeanConfig;
import com.bobocode.svydovets.service.base.CommonService;
import com.bobocode.svydovets.service.base.MessageService;
import org.junit.jupiter.api.*;
import svydovets.core.annotation.Bean;
import svydovets.core.annotation.Component;
import svydovets.util.BeanNameResolver;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled
public class BeanNameResolverTest {

    @Test
    @DisplayName("")
    public void test() {
        String beanName = MessageService.class.getAnnotation(Component.class).value();
        assertThat(beanName).isEqualTo(BeanNameResolver.resolveBeanNameByBeanType(MessageService.class));
    }

    @Test
    @DisplayName("")
    public void test1() {
        String beanName = CommonService.class.getSimpleName();
        assertThat(beanName).isEqualTo(BeanNameResolver.resolveBeanNameByBeanType(CommonService.class));
    }

    @Test
    @DisplayName("")
    public void testWithConfig() throws NoSuchMethodException {
        Class<BeanConfig> configClass = BeanConfig.class;
        var trimServiceMethod = configClass.getDeclaredMethod("trimService");
        var beanName = trimServiceMethod.getAnnotation(Bean.class).value();
        assertThat(beanName).isEqualTo(BeanNameResolver.resolveBeanNameByBeanInitMethod(trimServiceMethod));
    }

    @Test
    @DisplayName("")
    public void testWithConfig1() throws NoSuchMethodException {
        Class<BeanConfig> configClass = BeanConfig.class;
        var messageServiceMethod = configClass.getDeclaredMethod("messageService");
        var beanName = messageServiceMethod.getAnnotation(Bean.class).value();
        assertThat(beanName).isEqualTo(BeanNameResolver.resolveBeanNameByBeanInitMethod(messageServiceMethod));

    }
}
