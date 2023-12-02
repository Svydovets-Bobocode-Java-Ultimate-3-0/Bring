package com.bobocode.svydovets.ioc.core.autowire.injector;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;

import java.lang.reflect.Field;

import com.bobocode.svydovets.source.autowire.field.EditService;
import com.bobocode.svydovets.source.base.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import svydovets.core.context.injector.BeanInjector;
import svydovets.core.context.injector.InjectorConfig;
import svydovets.core.exception.AutowireBeanException;

class BeanInjectorTest {

  private BeanInjector beanInjector;
  private MessageService messageService;
  private InjectorConfig.Builder builder;
  private EditService editService;

  @BeforeEach
  void setUp() {
    beanInjector = new BeanInjector();
    messageService = new MessageService();
    editService = new EditService();

    builder =
        InjectorConfig.builder()
            .withBean(editService)
            .withBeanReceiver((beanType) -> messageService);
  }

  @Test
  void shouldInjectBean() {
    String expectedMessage = "test";
    messageService.setMessage(expectedMessage);

    Field field = editService.getClass().getDeclaredFields()[0];

    InjectorConfig injectorConfig = builder.withBeanField(field).build();

    beanInjector.inject(injectorConfig);

    assertThat(editService.getMessageService()).isNotNull();
    assertThat(editService.getMessageService().getMessage()).isEqualTo(expectedMessage);
  }

  @Test
  void shouldThrowExceptionWhenSettingBeanIsFailed() throws IllegalAccessException {
    Field field = Mockito.mock(Field.class);

    Mockito.doThrow(IllegalAccessException.class).when(field).set(any(), any());
    Mockito.when(field.getName()).thenReturn(messageService.getClass().getSimpleName());

    InjectorConfig injectorConfig = builder.withBeanField(field).build();

    assertThatExceptionOfType(AutowireBeanException.class)
        .isThrownBy(() -> beanInjector.inject(injectorConfig))
        .withMessage(
            String.format(
                "There is access to %s field", messageService.getClass().getSimpleName()));
  }
}
