package svydovets.core.annotation;

import svydovets.core.context.ApplicationContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *     Scope annotation is used to specify the scope of beans created from component.
 *     It allows to control the lifecycle and number of instances of each bean.
 * </p>
 * <p>
 *     Value attribute of Scope annotation specifies scope of bean that should be created.
 *     Default scope is singleton, which means that will create only one instance of bean,
 *     and all requests for that bean will return the same instance.
 *     If scope is prototype, new instance will be created every time bean is requested.
 * </p>
 * <p>
 *     Here is example of how to use Scope annotation:
 * </p>
 *
 * <pre class="code">
 * &#064;Component
 * &#064;Scope(ApplicationContext.SCOPE_SINGLETON)
 * public class SingletonCandidate {
 * }
 *
 * &#064;Component
 * &#064;Scope(ApplicationContext.SCOPE_PROTOTYPE)
 * public class PrototypeCandidate {
 * }
 * </pre>
 *
 * <p>
 *     In this example, SingletonCandidate class is annotated with @Scope(ApplicationContext.SCOPE_SINGLETON).
 *     This means that the framework will create only one instance of SingletonCandidate,
 *     and all requests for SingletonCandidate bean will return the same instance.
 * </p>
 * <p>
 *     The PrototypeCandidate class is annotated with @Scope(ApplicationContext.SCOPE_PROTOTYPE).
 *     This means that it will create new instance of PrototypeCandidate every time PrototypeCandidate bean is requested.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Scope {
    String value() default ApplicationContext.SCOPE_SINGLETON;
}
