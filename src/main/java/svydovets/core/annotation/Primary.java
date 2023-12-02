package svydovets.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *     Primary annotation is used to give higher preference to bean when there are multiple beans of the same type.
 *     It will use bean marked with @Primary when single candidate is required for autowiring.
 * </p>
 * <p>
 *     When class annotated with @Primary, it should prefer this bean when multiple candidates
 *     are qualified to autowire a single-valued dependency. If @Primary is not present,
 *     will choose bean to autowire by type or name, and if there's still ambiguity,
 *     it will throw a NoUniqueBeanDefinitionException.
 * </p>
 * <p>
 * Here is example of how to use the Primary annotation:
 * </p>
 * <pre class="code">
 * &#064;Primary
 * &#064;Component
 * public class PrimaryInjectionCandidate implements InjectionCandidate {
 * }
 * </pre>
 * <p>
 *     In this example, PrimaryInjectionCandidate class is annotated with @Primary.
 *     This means that when it needs to autowire bean of type InjectionCandidate,
 *     it will prefer to use instance of PrimaryInjectionCandidate, if available.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Primary {
}
