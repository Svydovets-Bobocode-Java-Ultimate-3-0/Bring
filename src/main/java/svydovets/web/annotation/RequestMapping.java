package svydovets.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *     RequestMapping annotation is used to map web requests onto specific handler classes and/or handler methods.
 *     It provides routing information and tells which URI matches which method.
 * </p>
 * <p>
 *     When class or a method annotated with @RequestMapping, this class or method should handle requests to certain URI.
 *     The value attribute of RequestMapping annotation specifies URI pattern that class or method will handle.
 * </p>
 * <p>
 *     Here is example of how to use the RequestMapping annotation:
 * </p>
 * <pre class="code">
 * &#064;RestController
 * &#064;RequestMapping("/users")
 * public class UserController {
 *     // handler methods
 * }
 * </pre>
 * <p>
 *     In this example, UserController class is annotated with @RequestMapping("/users").
 *     This means that this class will handle all requests that start with "/users".
 *     For example, GET request to "/users" might return list of users, while POST request to "/users" might create new user.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequestMapping {
    String value() default "";
}
