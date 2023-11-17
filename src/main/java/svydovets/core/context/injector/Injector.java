package svydovets.core.context.injector;

/**
 * The {@code Injector} interface defines a contract for classes that perform dependency injection.
 *
 * <p>Implementing classes are expected to provide an implementation for the {@code inject} method,
 * which is responsible for injecting dependencies into a target object based on the provided
 * configuration.
 *
 * <p>Usage example:
 *
 * <pre>
 *     Injector injector = new SomeInjector();
 *     InjectorConfig config = new InjectorConfig(...);
 *     injector.inject(config);
 * </pre>
 *
 * @author Oleksii
 * @version 1.0
 * @see InjectorConfig
 */
public interface Injector {

  /**
   * Injects dependencies into a target object based on the provided configuration.
   *
   * @param config The configuration specifying the target object, its field, and the strategy
   *               to obtain the dependency.
   */
    void inject(InjectorConfig config);

}
