package svydovets.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code PutMapping} annotation is used for mapping HTTP PUT requests onto specific handler methods.
 * This annotation is designed for methods within a web controller, indicating that the method should handle
 * PUT requests to a specified URI.
 *
 * <p>{@code PutMapping} is commonly applied to methods that handle updates to existing resources.
 * The {@code value} attribute specifies the URI pattern that the method is responsible for handling.
 * When a PUT request is made to this URI, the annotated method is called to process the request.
 *
 * <p>Usage example:
 * <pre>
 * {@code
 *   @PutMapping("/users/{id}")
 *   public User updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
 *       // Method implementation to update and return the user
 *   }
 * }
 * </pre>
 *
 * @author @Renat Safarov, @Oleksii Makieiev
 * @version 1.0
 * @see PathVariable
 * @see RequestParam
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PutMapping {
    String value() default "";
}
