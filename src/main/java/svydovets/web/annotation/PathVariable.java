package svydovets.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *     The PathVariable annotation is used to bind value of URI template variable to method parameter in controller.
 * </p>
 * <p>
 *     When method parameter annotated with @PathVariable, it takes certain part of URI and passit as parameter to method.
 *     The value attribute of PathVariable annotation specifies name of URI template variable to bind to.
 * </p>
 * <p>
 *     Here is example of how to use PathVariable annotation:
 * </p>
 * <pre class="code">
 * &#064;RestController
 * &#064;RequestMapping("/users")
 * public class UserController {
 *
 *     private AtomicLong idGenerator = new AtomicLong(0L);
 *     private Map<Long, User> userMap = new ConcurrentHashMap<>();
 *
 *     &#064;GetMapping("/{id}")
 *     public User getOneById(@PathVariable Long id) {
 *         return userMap.get(id);
 *     }
 * }
 * </pre>
 * <p>
 *     In this example, getOneById method in UserController class has parameter annotated with @PathVariable.
 *     This means that when request handles to "/users/{id}", it will take "id" part of URI, convert it to Long,
 *     and pass it as parameter to getOneById method.
 *     getOneById method then uses this id to retrieve corresponding User from userMap.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface PathVariable {
    String value() default "";
}
