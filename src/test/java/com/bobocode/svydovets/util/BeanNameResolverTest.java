package com.bobocode.svydovets.util;

import com.bobocode.svydovets.config.BeanConfigBase;
import com.bobocode.svydovets.service.base.CommonService;
import com.bobocode.svydovets.service.base.MessageService;
import lombok.SneakyThrows;
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
        assertThat(beanName).isEqualTo(BeanNameResolver.resolveBeanName(MessageService.class));
    }

    @Test
    @DisplayName("")
    public void test1() {
        String beanName = "commonService";
        assertThat(beanName).isEqualTo(BeanNameResolver.resolveBeanName(CommonService.class));
    }

    // todo: HOW TO HANDLE CHECKED EXCEPTION IN TEST METHODS??????????????????
    @Test
    @DisplayName("")
    public void testWithConfig() throws NoSuchMethodException {
        Class<BeanConfigBase> configClass = BeanConfigBase.class;
        var trimServiceMethod = configClass.getDeclaredMethod("trimService");
        var beanName = trimServiceMethod.getAnnotation(Bean.class).value();
        assertThat(beanName).isEqualTo(BeanNameResolver.resolveBeanName(trimServiceMethod));
    }

    @Test
    @DisplayName("")
    public void testWithConfig1() throws NoSuchMethodException {
        Class<BeanConfigBase> configClass = BeanConfigBase.class;
        var messageServiceMethod = configClass.getDeclaredMethod("messageService");
        var beanName = messageServiceMethod.getName();
        assertThat(beanName).isEqualTo(BeanNameResolver.resolveBeanName(messageServiceMethod));

    }
}
