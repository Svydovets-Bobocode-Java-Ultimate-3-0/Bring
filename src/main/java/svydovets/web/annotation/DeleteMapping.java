package svydovets.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code DeleteMapping} annotation is used to map HTTP DELETE requests onto specific handler methods.
 * This annotation is specifically targeted to be used on methods.
 *
 * <p>Typically, {@code DeleteMapping} is used to mark a method in a controller that should handle a
 * DELETE request to a specified URI. The {@code value} attribute of the annotation defines this URI.
 *
 * <p>Usage example:
 * <pre>
 * {@code
 *     @DeleteMapping("/{id}")
 *     public void removeUser(@PathVariable Long id) {
 *         // Method implementation
 *     }
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DeleteMapping {
    String value() default "";
}
