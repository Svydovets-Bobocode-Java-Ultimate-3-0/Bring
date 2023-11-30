package svydovets.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The @Component annotation is used to indicate that a class is component of context.
 * It is a generic annotation that designates a class as bean, allowing IoC
 * container to manage its lifecycle. Components are typically Java classes that encapsulate business logic or other
 * processing capabilities within a user application.
 *
 * <h2>Purpose</h2>
 * <ul>
 *     <li>The primary purpose of the @Component annotation is to identify a class as bean or component.
 *     It allows the class to be automatically detected and registered during the component scanning process.</li>
 * </ul>
 *
 * <h2>Bean Naming</h2>
 * <p>By default, the bean name is generated based on the class name with the initial lowercase letter (e.g., a class named MyComponent becomes the bean with the name myComponent).
 * Custom bean names can be specified using the value attribute of the @Component annotation.</p>
 *
 * <pre class="code">
 * &#064;Component
 * public class MyComponentWithDefaultName {
 * }
 *
 * &#064;Component("customName")
 * public class MyComponentWithCustomName {
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Component {
    String value() default "";
}
