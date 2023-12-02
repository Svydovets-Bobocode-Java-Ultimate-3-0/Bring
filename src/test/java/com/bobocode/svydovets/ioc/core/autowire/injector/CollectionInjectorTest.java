package com.bobocode.svydovets.ioc.core.autowire.injector;

import com.bobocode.svydovets.source.collection.CustomCollection;
import com.bobocode.svydovets.source.collection.CustomCollectionHolderService;
import com.bobocode.svydovets.source.collection.WildcardTypeCollection;
import com.bobocode.svydovets.source.qualifier.withPrimary.PrimaryProductServiceImpl;
import com.bobocode.svydovets.source.qualifier.withPrimary.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import svydovets.core.context.injector.CollectionInjector;
import svydovets.core.context.injector.InjectorConfig;
import svydovets.core.exception.BeanCreationException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static com.bobocode.svydovets.source.autowire.collection.CollectionsHolderService.ListHolderService;
import static com.bobocode.svydovets.source.autowire.collection.CollectionsHolderService.SetHolderService;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class CollectionInjectorTest {

    private CollectionInjector collectionInjector;
    private ProductService productService;

    private InjectorConfig.Builder builder;

    @BeforeEach
    void setUp() {
        collectionInjector = new CollectionInjector();
        productService = new PrimaryProductServiceImpl();

        builder =
                InjectorConfig.builder()
                        .withBeanReceiver((beanType) -> productService)
                        .withBeanOfTypeReceiver(
                                (beanType) ->
                                        Map.of(productService.getClass().getSimpleName(), List.of(productService)));
    }

    @Test
    void shouldInjectListOfBeans() {
        ListHolderService listHolderService = new ListHolderService();

        Field field = listHolderService.getClass().getDeclaredFields()[0];

        InjectorConfig injectorConfig =
                builder.withBean(listHolderService).withBeanField(field).build();

        collectionInjector.inject(injectorConfig);

        assertThat(listHolderService.getProductServiceList()).isNotNull();
        assertThat(listHolderService.getProductServiceList().size()).isEqualTo(1);
    }

    @Test
    void shouldInjectSetOfBeans() {
        SetHolderService setHolderService = new SetHolderService();

        Field field = setHolderService.getClass().getDeclaredFields()[0];

        InjectorConfig injectorConfig = builder.withBean(setHolderService).withBeanField(field).build();

        collectionInjector.inject(injectorConfig);

        assertThat(setHolderService.getProductServiceSet()).isNotNull();
        assertThat(setHolderService.getProductServiceSet().size()).isEqualTo(1);
    }

    @Test
    void shouldThrowExceptionWhenCollectionHasRawType() {
        SetHolderService setHolderService = new SetHolderService();

        Field field = Mockito.mock(Field.class);

        Mockito.doReturn(Map.class).when(field).getType();

        InjectorConfig injectorConfig = builder.withBean(setHolderService).withBeanField(field).build();

        assertThatExceptionOfType(BeanCreationException.class)
                .isThrownBy(() -> collectionInjector.inject(injectorConfig))
                .withMessage(
                        "Don't use raw types for collections. Raw type founded for field null of null class");
    }

    @Test
    void shouldThrowExceptionWhenCollectionIsNotSupported() {
        CustomCollectionHolderService customCollectionHolderService =
                new CustomCollectionHolderService();

        Field field = customCollectionHolderService.getClass().getDeclaredFields()[0];

        InjectorConfig injectorConfig =
                builder.withBean(customCollectionHolderService).withBeanField(field).build();

        assertThatExceptionOfType(BeanCreationException.class)
                .isThrownBy(() -> collectionInjector.inject(injectorConfig))
                .withMessage(String.format(
                        "We don't support dependency injection into collection of type: %s",
                        CustomCollection.class.getName())
                );
    }

    @Test
    void shouldThrowExceptionWhenCollectionHasWildcardType() {
        WildcardTypeCollection wildcardTypeCollection = new WildcardTypeCollection();

        Field field = wildcardTypeCollection.getClass().getDeclaredFields()[0];

        InjectorConfig injectorConfig =
                builder.withBean(wildcardTypeCollection).withBeanField(field).build();

        assertThatExceptionOfType(BeanCreationException.class)
                .isThrownBy(() -> collectionInjector.inject(injectorConfig))
                .withMessage("Don't use wildcard for collections. Wildcard found for bean of type null");
    }
}
