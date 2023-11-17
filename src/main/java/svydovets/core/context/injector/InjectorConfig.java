package svydovets.core.context.injector;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Function;

/**
 * The {@code InjectorConfig} class encapsulates configuration details required for dependency injection.
 * It includes information about the target bean, its field, and the strategies to obtain dependencies
 * for injection, both for single beans and for beans represented as maps.
 *
 * <p>Instances of this class are typically created using the {@code Builder} pattern, providing a
 * convenient and readable way to construct configuration objects.</p>
 *
 * <p>Usage example:</p>
 * <pre>
 *     // Create an instance of InjectorConfig using the Builder pattern
 *     InjectorConfig config = InjectorConfig.builder()
 *             .withBean(targetBean)
 *             .withBeanField(targetField)
 *             .withBeanReceiver(beanReceiverFunction)
 *             .withBeanOfTypeReceiver(beanOfTypeReceiverFunction)
 *             .build();
 * </pre>
 *
 * @author Oleksii
 * @see InjectorExecutor
 * @see AbstractInjector
 */
public class InjectorConfig {

  private final Object bean;
  private final Field beanField;

  private final Function<Class<?>, Object> beanReceiver;

  private final Function<Class<?>, Map<String, ?>> beanOfTypeReceiver;

  /**
   * Private constructor to enforce the use of the {@code Builder} pattern for object creation.
   *
   * @param bean               The target bean.
   * @param beanField          The field within the target bean for dependency injection.
   * @param beanReceiver       The function to obtain a single bean for injection.
   * @param beanOfTypeReceiver The function to obtain a map of beans for injection, with keys representing bean names.
   */
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

  /**
   * Returns a builder for creating instances of {@code InjectorConfig}.
   *
   * @return A new instance of {@code Builder}.
   */
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

  /**
   * The {@code Builder} class provides a convenient way to construct instances of {@code InjectorConfig}.
   */
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
