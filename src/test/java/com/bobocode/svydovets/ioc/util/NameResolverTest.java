package com.bobocode.svydovets.ioc.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.bobocode.svydovets.source.base.CommonService;
import com.bobocode.svydovets.source.base.MessageService;
import com.bobocode.svydovets.source.config.BasePackageBeansConfig;
import com.bobocode.svydovets.source.config.BasePackageWithAdditionalBeansConfig;
import com.bobocode.svydovets.source.interfaces.NonInterfaceConfiguration;
import com.bobocode.svydovets.source.web.AllMethodRestController;
import com.bobocode.svydovets.source.web.SimpleRestController;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import svydovets.core.annotation.Bean;
import svydovets.core.annotation.Component;
import svydovets.core.annotation.Configuration;
import svydovets.util.NameResolver;
import svydovets.web.annotation.RestController;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NameResolverTest {

    @Test
    public void shouldReturnComponentAnnotationValueIfNameIsSpecifiedExplicitly() {
        String beanName = MessageService.class.getAnnotation(Component.class).value();
        assertThat(beanName).isEqualTo(NameResolver.resolveBeanName(MessageService.class));
    }

    @Test
    public void shouldReturnSimpleClassNameIfComponentAnnotationValueIsNotSpecifiedExplicitly() {
        String beanName = "commonService";
        assertThat(beanName).isEqualTo(NameResolver.resolveBeanName(CommonService.class));
    }

    @Test
    public void shouldReturnConfigurationAnnotationValueIfNameIsSpecifiedExplicitly() {
        String beanName = NonInterfaceConfiguration.class.getAnnotation(Configuration.class).value();
        assertThat(beanName).isEqualTo(NameResolver.resolveBeanName(NonInterfaceConfiguration.class));
    }

    @Test
    public void shouldReturnSimpleClassNameIfConfigurationAnnotationValueIsNotSpecifiedExplicitly() {
        String beanName = "basePackageBeansConfig";
        assertThat(beanName).isEqualTo(NameResolver.resolveBeanName(BasePackageBeansConfig.class));
    }

    @Test
    public void shouldReturnControllerAnnotationValueIfNameIsSpecifiedExplicitly() {
        String beanName = SimpleRestController.class.getAnnotation(RestController.class).value();
        assertThat(beanName).isEqualTo(NameResolver.resolveBeanName(SimpleRestController.class));
    }

    @Test
    public void shouldReturnSimpleClassNameIfControllerAnnotationValueIsNotSpecifiedExplicitly() {
        String beanName = "allMethodRestController";
        assertThat(beanName).isEqualTo(NameResolver.resolveBeanName(AllMethodRestController.class));
    }

    @Test
    public void shouldReturnBeanAnnotationValueIfNameIsSpecifiedExplicitly() throws NoSuchMethodException {
        Class<BasePackageWithAdditionalBeansConfig> configClass = BasePackageWithAdditionalBeansConfig.class;
        var trimServiceMethod = configClass.getDeclaredMethod("trimService");
        var beanName = trimServiceMethod.getAnnotation(Bean.class).value();
        assertThat(beanName).isEqualTo(NameResolver.resolveBeanName(trimServiceMethod));
    }

    @Test
    public void shouldReturnMethodNameIfBeanAnnotationValueIsNotSpecifiedExplicitly() throws NoSuchMethodException {
        Class<BasePackageWithAdditionalBeansConfig> configClass = BasePackageWithAdditionalBeansConfig.class;
        var messageServiceMethod = configClass.getDeclaredMethod("orderService");
        var beanName = messageServiceMethod.getName();
        assertThat(beanName).isEqualTo(NameResolver.resolveBeanName(messageServiceMethod));
    }
}
