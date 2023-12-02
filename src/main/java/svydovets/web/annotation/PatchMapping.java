package svydovets.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code PatchMapping} annotation is designed for mapping HTTP PATCH requests to specific handler methods.
 * This annotation is targeted for use on methods in a web controller, indicating that the method should handle
 * PATCH requests for a specified URI.
 *
 * <p>By using {@code PatchMapping}, a method is designated to process incoming PATCH requests, typically used
 * to send only the changes to a resource rather than the complete representation of the resource.
 * The {@code value} attribute specifies the URI pattern to which the method should respond.
 *
 * <p>Usage example:
 * <pre>
 * {@code
 *   @PatchMapping("/users")
 *   public User createUser(@RequestBody UpdatedUserDto updatedUserDto) {
 *       // Method implementation to update and return an updated user
 *   }
 * }
 * </pre>
 *
 * @see RequestBody
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PatchMapping {
    String value() default "";
}
