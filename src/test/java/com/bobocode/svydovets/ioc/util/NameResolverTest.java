package com.bobocode.svydovets.ioc.util;

import com.bobocode.svydovets.source.base.CommonService;
import com.bobocode.svydovets.source.base.MessageService;
import com.bobocode.svydovets.source.config.BasePackageWithAdditionalBeansConfig;
import com.bobocode.svydovets.source.qualifier.withoutPrimary.PaymentService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import svydovets.core.annotation.Bean;
import svydovets.core.annotation.Component;
import svydovets.exception.NoSuchBeanException;
import svydovets.exception.NoUniqueBeanException;
import svydovets.util.NameResolver;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static svydovets.util.ErrorMessages.NO_UNIQUE_BEAN_FOUND_OF_TYPE;

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

    @Test
    public void shouldThrowNoSuchBeanExceptionIfMethodDoesNotDeclaredAsBean() throws NoSuchMethodException {
        Class<BasePackageWithAdditionalBeansConfig> configClass = BasePackageWithAdditionalBeansConfig.class;
        var messageServiceMethod = configClass.getDeclaredMethod("nullService");
        assertThatExceptionOfType(NoSuchBeanException.class)
            .isThrownBy(() -> NameResolver.resolveBeanName(messageServiceMethod))
            .withMessage(String.format("Method %s is not defined as a bean method", messageServiceMethod.getName()));
    }
}
