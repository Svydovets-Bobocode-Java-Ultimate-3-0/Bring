package svydovets.core.context.injector;

import svydovets.exception.NoSuchBeanDefinitionException;
import svydovets.exception.NoSuchBeanException;

import java.util.*;

public class InjectorExecutor {

  private static final Injector beanInjector;
  private static final Map<Class<?>, Injector> injectors = new HashMap<>();

  static {
    beanInjector = new BeanInjector();
    injectors.put(Collection.class, new CollectionInjector());
    injectors.put(Map.class, new MapOfBeansInjector());
  }

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
