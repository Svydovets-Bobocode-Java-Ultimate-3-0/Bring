package svydovets.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code PostMapping} annotation is designed for mapping HTTP POST requests to specific handler methods.
 * This annotation is targeted for use on methods in a web controller, indicating that the method should handle
 * POST requests for a specified URI.
 *
 * <p>By using {@code PostMapping}, a method is designated to process incoming POST requests, typically used
 * for creating new resources or submitting data. The {@code value} attribute specifies the URI pattern to which
 * the method should respond.
 *
 * <p>Usage example:
 * <pre>
 * {@code
 *   @PostMapping("/users")
 *   public User createUser(@RequestBody User newUser) {
 *       // Method implementation to create and return a new user
 *   }
 * }
 * </pre>
 *
 * @author [Your Name]
 * @version 1.0
 * @since [Version of your framework or the date of creation]
 * @see RequestBody
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PostMapping {
    String value() default "";
}
