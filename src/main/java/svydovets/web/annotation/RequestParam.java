package svydovets.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *     RequestParam annotation is used to bind HTTP request parameters to method parameter in controller.
 * </p>
 * <p>
 *     When method parameter annotated with @RequestParam, it takes a certain parameter from HTTP request and pass
 *     it as parameter to method.
 *     Value attribute of RequestParam annotation specifies name of request parameter to bind to.
 * </p>
 * <p>
 *     Here is example of how to use the RequestParam annotation:
 * </p>
 * <pre class="code">
 * &#064;RestController
 * &#064;RequestMapping("/users")
 * public class UserController {
 *     &#064;GetMapping
 *     public User getOneByFirstName(@RequestParam String firstName) {
 *         return userMap.values()
 *                 .stream()
 *                 .filter(user -> user.getFirstName().equals(firstName))
 *                 .findAny()
 *                 .orElseThrow();
 *     }
 * }
 * </pre>
 * <p>
 *     In this example, getOneByFirstName method in UserController class has parameter annotated with @RequestParam.
 *     This means that when it handles GET request to "/users", it will take "firstName" parameter from request,
 *     convert it to String, and pass it as parameter to the getOneByFirstName method.
 *     getOneByFirstName method then uses this firstName to retrieve corresponding User from userMap.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestParam {
    String value() default "";
}
