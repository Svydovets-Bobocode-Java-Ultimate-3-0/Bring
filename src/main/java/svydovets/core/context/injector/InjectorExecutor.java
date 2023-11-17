package svydovets.core.context.injector;

import svydovets.exception.NoSuchBeanDefinitionException;
import svydovets.exception.NoSuchBeanException;

import java.util.*;

/**
 * The {@code InjectorExecutor} class is responsible for executing dependency injection
 * based on the provided {@code InjectorConfig}. It uses a set of predefined injectors,
 * including a default bean injector, a collection injector, and a map injector,
 * to handle different types of injection scenarios.
 *
 * <p>The class provides a static method {@code execute} to perform the injection.
 * It determines the appropriate injector based on the type of the target bean field
 * and delegates the injection process to the selected injector.</p>
 *
 * <p>Usage example:</p>
 * <pre>
 *     InjectorConfig config = new InjectorConfig(...);
 *     InjectorExecutor.execute(config);
 * </pre>
 *
 * @author Oleksii
 * @see InjectorConfig
 * @see Injector
 * @see BeanInjector
 * @see CollectionInjector
 * @see MapOfBeansInjector
 */
public class InjectorExecutor {

  private static final Injector beanInjector;
  private static final Map<Class<?>, Injector> injectors = new HashMap<>();

  static {
    beanInjector = new BeanInjector();
    injectors.put(Collection.class, new CollectionInjector());
    injectors.put(Map.class, new MapOfBeansInjector());
  }

  /**
   * Executes the dependency injection based on the provided {@code InjectorConfig}.
   * The method determines the appropriate injector based on the type of the target bean field
   * and delegates the injection process to the selected injector.
   *
   * @param injectorConfig The configuration specifying the target bean, its field, and the strategy
   *                       to obtain the dependency.
   * @throws NoSuchBeanException         if no suitable injector is found for the given field type.
   * @throws NoSuchBeanDefinitionException if no bean definition is available for the given field type.
   */
  public static void execute(InjectorConfig injectorConfig) {

    var autowireCandidateType = injectorConfig.getBeanField().getType();

    try {
      beanInjector.inject(injectorConfig);
    } catch (NoSuchBeanException | NoSuchBeanDefinitionException e) {
      Set<Class<?>> keys = injectors.keySet();

      Class<?> foundKey = keys.stream()
              .filter(key -> key.isAssignableFrom(autowireCandidateType))
              .findFirst()
              .orElseThrow(() -> e);

      injectors.get(foundKey).inject(injectorConfig);
    }
  }
}
