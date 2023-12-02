package com.bobocode.svydovets.ioc.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static svydovets.util.NameResolver.resolveBeanName;
import static svydovets.util.NameResolver.resolveRequestParameterName;

import java.util.Arrays;

import com.bobocode.svydovets.source.base.CommonService;
import com.bobocode.svydovets.source.base.MessageService;
import com.bobocode.svydovets.source.config.BasePackageBeansConfig;
import com.bobocode.svydovets.source.config.BasePackageWithAdditionalBeansConfig;
import com.bobocode.svydovets.source.interfaces.NonInterfaceConfiguration;
import com.bobocode.svydovets.source.web.AllMethodRestController;
import com.bobocode.svydovets.source.web.SimpleRestController;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import svydovets.core.annotation.Bean;
import svydovets.core.annotation.Component;
import svydovets.core.annotation.Configuration;
import svydovets.web.annotation.PathVariable;
import svydovets.web.annotation.RequestParam;
import svydovets.web.annotation.RestController;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NameResolverTest {

    @Test
    @Order(1)
    public void shouldReturnComponentAnnotationValueIfNameIsSpecifiedExplicitly() {
        String beanName = MessageService.class.getAnnotation(Component.class).value();
        assertThat(beanName).isEqualTo(resolveBeanName(MessageService.class));
    }

    @Test
    @Order(2)
    public void shouldReturnSimpleClassNameIfComponentAnnotationValueIsNotSpecifiedExplicitly() {
        String beanName = "commonService";
        assertThat(beanName).isEqualTo(resolveBeanName(CommonService.class));
    }

    @Test
    @Order(3)
    public void shouldReturnConfigurationAnnotationValueIfNameIsSpecifiedExplicitly() {
        String beanName = NonInterfaceConfiguration.class.getAnnotation(Configuration.class).value();
        assertThat(beanName).isEqualTo(resolveBeanName(NonInterfaceConfiguration.class));
    }

    @Test
    @Order(4)
    public void shouldReturnSimpleClassNameIfConfigurationAnnotationValueIsNotSpecifiedExplicitly() {
        String beanName = "basePackageBeansConfig";
        assertThat(beanName).isEqualTo(resolveBeanName(BasePackageBeansConfig.class));
    }

    @Test
    @Order(5)
    public void shouldReturnControllerAnnotationValueIfNameIsSpecifiedExplicitly() {
        String beanName = SimpleRestController.class.getAnnotation(RestController.class).value();
        assertThat(beanName).isEqualTo(resolveBeanName(SimpleRestController.class));
    }

    @Test
    @Order(6)
    public void shouldReturnSimpleClassNameIfControllerAnnotationValueIsNotSpecifiedExplicitly() {
        String beanName = "allMethodRestController";
        assertThat(beanName).isEqualTo(resolveBeanName(AllMethodRestController.class));
    }

    @Test
    @Order(7)
    public void shouldReturnBeanAnnotationValueIfNameIsSpecifiedExplicitly() throws NoSuchMethodException {
        Class<BasePackageWithAdditionalBeansConfig> configClass = BasePackageWithAdditionalBeansConfig.class;
        var trimServiceMethod = configClass.getDeclaredMethod("trimService");
        var beanName = trimServiceMethod.getAnnotation(Bean.class).value();
        assertThat(beanName).isEqualTo(resolveBeanName(trimServiceMethod));
    }

    @Test
    @Order(8)
    public void shouldReturnMethodNameIfBeanAnnotationValueIsNotSpecifiedExplicitly() throws NoSuchMethodException {
        Class<BasePackageWithAdditionalBeansConfig> configClass = BasePackageWithAdditionalBeansConfig.class;
        var messageServiceMethod = configClass.getDeclaredMethod("orderService");
        var beanName = messageServiceMethod.getName();
        assertThat(beanName).isEqualTo(resolveBeanName(messageServiceMethod));
    }

    @Test
    @Order(9)
    public void shouldReturnPathVariableValueIfParameterIsSpecifiedExplicitly() throws NoSuchMethodException {
        Class<SimpleRestController> configClass = SimpleRestController.class;
        var method = configClass.getDeclaredMethod("helloPath", String.class);
        var firstParameter = method.getParameters()[0];
        var parameterName = firstParameter.getName();
        assertThat(parameterName).isEqualTo(resolveRequestParameterName(firstParameter));
    }

    @Test
    @Order(10)
    public void shouldReturnPathVariableValueIfParameterIsNotSpecifiedExplicitly() throws NoSuchMethodException {
        Class<SimpleRestController> configClass = SimpleRestController.class;
        var method = configClass.getDeclaredMethod("goodbyePath", String.class);
        var firstParameter = method.getParameters()[0];
        var parameterName = firstParameter.getAnnotation(PathVariable.class).value();
        assertThat(parameterName).isEqualTo(resolveRequestParameterName(firstParameter));
    }

    @Test
    @Order(11)
    public void shouldReturnRequestParamValueIfParameterIsSpecifiedExplicitly() throws NoSuchMethodException {
        Class<SimpleRestController> configClass = SimpleRestController.class;
        var method = configClass.getDeclaredMethod("helloRequest", String.class);
        var firstParameter = method.getParameters()[0];
        var parameterName = firstParameter.getName();
        assertThat(parameterName).isEqualTo(resolveRequestParameterName(firstParameter));
    }

    @Test
    @Order(12)
    public void shouldReturnRequestParamValueIfParameterIsNotSpecifiedExplicitly() throws NoSuchMethodException {
        Class<SimpleRestController> configClass = SimpleRestController.class;
        var method = configClass.getDeclaredMethod("goodbyeRequest", String.class);
        var firstParameter = method.getParameters()[0];
        var parameterName = firstParameter.getAnnotation(RequestParam.class).value();
        assertThat(parameterName).isEqualTo(resolveRequestParameterName(firstParameter));
    }
}
