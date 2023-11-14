package com.bobocode.svydovets.util;

import com.bobocode.svydovets.config.BeanConfig;
import com.bobocode.svydovets.service.base.CommonService;
import com.bobocode.svydovets.service.base.MessageService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import svydovets.core.annotation.Bean;
import svydovets.core.annotation.Component;
import svydovets.core.context.ApplicationContext;
import svydovets.core.context.DefaultApplicationContext;
import svydovets.util.BeanNameResolver;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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

    // todo: HOW TO HANDLE CHECKED EXCEPTION IN TEST METHODS??????????????????
    @Test
    @DisplayName("")
    @SneakyThrows
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
