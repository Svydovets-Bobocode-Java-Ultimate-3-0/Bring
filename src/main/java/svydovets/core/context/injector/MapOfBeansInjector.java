package svydovets.core.context.injector;

import java.lang.reflect.Field;

/**
 * The {@code MapOfBeansInjector} class is an implementation of the {@code AbstractInjector}
 * designed specifically for injecting dependencies into maps within a target bean.
 *
 * <p>This injector uses a configuration provided through an {@code InjectorConfig} object to
 * determine the target bean, its field, and the strategy to obtain the dependencies for injection
 * into the map. The actual injection is performed by invoking the {@code inject} method.
 *
 * <p>The injection process involves retrieving the type of the map field, obtaining the existing
 * map from the target bean, and injecting the dependencies into the map. If the map field is
 * already created by the user, the dependencies are added to the existing map. Otherwise, a new map
 * instance is created, and the dependencies are injected into it.
 *
 * <p>Usage example:
 *
 * <pre>
 *     MapOfBeansInjector mapOfBeansInjector = new MapOfBeansInjector();
 *     InjectorConfig config = new InjectorConfig(...);
 *     mapOfBeansInjector.inject(config);
 * </pre>
 *
 * @author Oleksii
 * @see AbstractInjector
 * @see InjectorConfig
 */
public class MapOfBeansInjector extends AbstractInjector {

  /**
   * Injects dependencies into a map field of a target bean based on the provided configuration.
   * If the map field is already created by the user, the dependencies are added to the existing map.
   * Otherwise, a new map instance is created, and the dependencies are injected into it.
   *
   * @param config The configuration specifying the target bean, its map field, and the strategy
   *               to obtain the dependencies.
   */
  @Override
  public void inject(InjectorConfig config) {
    Object bean = config.getBean();
    Field field = config.getBeanField();

    Class<?> autowireCandidateType = retrieveAutowireCandidateType(field);
    var mapOfBeansForInjection = retrieveFieldValue(bean, field);
    var mapOfBeansToInject = config.getBeanOfTypeReceiver().apply(autowireCandidateType);
    setDependency(bean, field, mapOfBeansToInject);
  }
}
