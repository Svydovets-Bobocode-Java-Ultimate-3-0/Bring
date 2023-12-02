package svydovets.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *     Qualifier annotation is used to resolve ambiguity when multiple beans of the same type are available for autowiring.
 *     It allows for more fine-grained control over where and how autowiring should be accomplished.
 * </p>
 * <p>
 *     When you annotate field, method parameter, or method with @Qualifier, you're telling to inject bean with specific name.
 *     The Qualifier annotation's value attribute specifies the bean's name that should be wired.
 * </p>
 * <p>
 *     Here is example of how to use the Qualifier annotation:
 * </p>
 * <pre class="code">
 * &#064;Component
 * public class OrderService {
 *     &#064;Autowired
 *     &#064;Qualifier("storeItem")
 *     private Item item;
 *
 *     public Item getItem() {
 *         return item;
 *     }
 * }
 * </pre>
 * <p>
 *     In this example, the item field in the OrderService class is annotated with@Autowired
 *     and @Qualifier("storeItem").
 *     This means that when it autowires the item field, it will look for
 *     bean named "storeItem" and inject it.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface Qualifier {
    String value() default "";
}
