package svydovets.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code GetMapping} annotation is used for mapping HTTP GET requests to specific handler methods.
 * This annotation is intended for use on methods within a web controller class.
 *
 * <p>{@code GetMapping} is typically applied to a public method in a controller to denote that it should
 * handle a GET request for a specified URI. The {@code value} attribute of the annotation defines this URI.
 * When an HTTP GET request is made to the specified URI, the annotated method is invoked to process the request.
 *
 * <p>Usage example:
 * <pre>
 * {@code
 *   @GetMapping("/users/{id}")
 *   public User getUser(@PathVariable Long id) {
 *       // Method implementation to return a user
 *   }
 * }
 * </pre>
 *
 * <p>Note: This annotation assumes the presence of a framework capable of processing HTTP requests and
 * routing them to methods annotated with {@code GetMapping}, similar to how the Spring Framework operates.
 *
 * @author @Renat Safarov
 * @version 1.0
 * @see PathVariable
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GetMapping {
    String value() default "";
}
