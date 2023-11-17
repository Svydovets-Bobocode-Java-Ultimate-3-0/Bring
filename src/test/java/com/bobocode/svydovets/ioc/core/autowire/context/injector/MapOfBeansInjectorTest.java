package com.bobocode.svydovets.core.context.injector;

import com.bobocode.svydovets.service.base.CollectionsHolderService.MapHolderService;
import com.bobocode.svydovets.service.collection.IncorrectMapHolderService;
import com.bobocode.svydovets.service.qualifier.PrimaryProductServiceImpl;
import com.bobocode.svydovets.service.qualifier.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import svydovets.core.context.injector.InjectorConfig;
import svydovets.core.context.injector.MapOfBeansInjector;
import svydovets.exception.BeanCreationException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

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
  void shouldInjectMapOfBeans() {
    MapHolderService mapHolderService = new MapHolderService();

    Field field = mapHolderService.getClass().getDeclaredFields()[0];

    InjectorConfig injectorConfig = builder.withBean(mapHolderService).withBeanField(field).build();

    mapOfBeansInjector.inject(injectorConfig);

    assertThat(mapHolderService.getProductServiceMap()).isNotNull();
    assertThat(mapHolderService.getProductServiceMap().containsKey("PrimaryProductServiceImpl"))
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
