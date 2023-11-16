package svydovets.core.context.injector;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Function;

public class InjectorConfig {

  private final Object bean;
  private final Field beanField;

  private final Function<Class<?>, Object> beanReceiver;

  private final Function<Class<?>, Map<String, ?>> beanOfTypeReceiver;

  public InjectorConfig(
      Object bean,
      Field beanField,
      Function<Class<?>, Object> beanReceiver,
      Function<Class<?>, Map<String, ?>> beanOfTypeReceiver) {
    this.bean = bean;
    this.beanField = beanField;
    this.beanReceiver = beanReceiver;
    this.beanOfTypeReceiver = beanOfTypeReceiver;
  }

  public static Builder builder() {
      return new Builder();
  }

  public Object getBean() {
    return bean;
  }

  public Field getBeanField() {
    return beanField;
  }

  public Function<Class<?>, Object> getBeanReceiver() {
    return beanReceiver;
  }

  public Function<Class<?>, Map<String, ?>> getBeanOfTypeReceiver() {
    return beanOfTypeReceiver;
  }

  public static class Builder {

    private Object bean;
    private Field beanField;

    private Function<Class<?>, Object> beanReceiver;

    private Function<Class<?>, Map<String, ?>> beanOfTypeReceiver;

    public Builder withBean(Object bean) {
      this.bean = bean;
      return this;
    }

    public Builder withBeanField(Field beanField) {
      this.beanField = beanField;
      return this;
    }

    public Builder withBeanReceiver(Function<Class<?>, Object> beanReceiver) {
      this.beanReceiver = beanReceiver;
      return this;
    }

    public Builder withBeanOfTypeReceiver(
        Function<Class<?>, Map<String, ?>> beanOfTypeReceiver) {
      this.beanOfTypeReceiver = beanOfTypeReceiver;
      return this;
    }

    public InjectorConfig build() {
      return new InjectorConfig(bean, beanField, beanReceiver, beanOfTypeReceiver);
    }
  }
}
