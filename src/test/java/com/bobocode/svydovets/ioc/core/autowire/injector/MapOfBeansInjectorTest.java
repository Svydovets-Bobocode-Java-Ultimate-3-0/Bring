package com.bobocode.svydovets.ioc.core.autowire.injector;

import com.bobocode.svydovets.source.collection.IncorrectMapHolderService;
import com.bobocode.svydovets.source.qualifier.withPrimary.PrimaryProductServiceImpl;
import com.bobocode.svydovets.source.qualifier.withPrimary.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import svydovets.core.context.injector.InjectorConfig;
import svydovets.core.context.injector.MapOfBeansInjector;
import svydovets.exception.BeanCreationException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static com.bobocode.svydovets.source.autowire.collection.CollectionsHolderService.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class MapOfBeansInjectorTest {

    private MapOfBeansInjector mapOfBeansInjector;
    private ProductService productService;

    private InjectorConfig.Builder builder;

    @BeforeEach
    void setUp() {
        mapOfBeansInjector = new MapOfBeansInjector();
        productService = new PrimaryProductServiceImpl();
        builder =
                InjectorConfig.builder()
                        .withBeanReceiver((classType) -> productService)
                        .withBeanOfTypeReceiver(
                                (beanType) ->
                                        Map.of(productService.getClass().getSimpleName(), List.of(productService)));
    }

    @Test
    void shouldInjectIntoUninitializedMapOfBeans() {
        MapHolderService mapHolderService = new MapHolderService();

        Field field = mapHolderService.getClass().getDeclaredFields()[0];

        InjectorConfig injectorConfig = builder.withBean(mapHolderService).withBeanField(field).build();

        mapOfBeansInjector.inject(injectorConfig);

        assertThat(mapHolderService.getProductServiceMap()).isNotNull();
        assertThat(mapHolderService.getProductServiceMap().containsKey("PrimaryProductServiceImpl"))
                .isTrue();
    }

    @Test
    void shouldInjectIntoInitializedMapOfBeans() {
        MapHolderService mapHolderService = new MapHolderService();

        Field field = mapHolderService.getClass().getDeclaredFields()[1];

        InjectorConfig injectorConfig = builder.withBean(mapHolderService).withBeanField(field).build();

        mapOfBeansInjector.inject(injectorConfig);

        assertThat(mapHolderService.getInitializedProductServiceMap()).isNotNull();
        assertThat(mapHolderService.getInitializedProductServiceMap().containsKey("PrimaryProductServiceImpl"))
                .isTrue();
    }

    @Test
    void shouldThrowExceptionWhenKeyIsNotString() {
        IncorrectMapHolderService incorrectMapHolderService = new IncorrectMapHolderService();

        Field field = incorrectMapHolderService.getClass().getDeclaredFields()[0];

        InjectorConfig injectorConfig =
                builder.withBean(incorrectMapHolderService).withBeanField(field).build();

        assertThatExceptionOfType(BeanCreationException.class)
                .isThrownBy(() -> mapOfBeansInjector.inject(injectorConfig))
                .withMessage("We processing Map only with String key type");
    }
}
