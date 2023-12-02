package svydovets.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *     RequestBody annotation is used to bind HTTP request body with method parameter in controller.
 * </p>
 * <p>
 *     When method parameter annotated with @RequestBody, it converts body of HTTP request to type of method parameter.
 *     It will convert body of request to declared method argument type.
 * </p>
 * <p>
 *     Here is example of how to use the RequestBody annotation:
 * </p>
 * <pre class="code">
 * &#064;RestController
 * &#064;RequestMapping("/users")
 * public class UserController {
 *
 *     &#064;PostMapping("/")
 *     public User createUser(@RequestBody User user) {
 *     }
 * }
 * </pre>
 * <p>
 *     In this example, createUser method in UserController class has parameter annotated with @RequestBody.
 *     This means that when it handles POST request to "/users/", it will take body of request,
 *     convert it to User object, and pass it as parameter to createUser method.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestBody {
    String value() default "";
}
