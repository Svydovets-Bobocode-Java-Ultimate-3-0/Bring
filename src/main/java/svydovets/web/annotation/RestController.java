package svydovets.web.annotation;

import svydovets.core.annotation.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code @RestController} annotation is a custom Java annotation used to mark classes as REST controllers. It
 * signifies that the annotated class is responsible for handling HTTP requests and generating REST responses. This
 * annotation is designed to be retained at runtime, allowing for reflection and processing during program execution.
 *
 * <p>When applied to a class, the {@code @RestController} annotation indicates that instances of the class act as
 * controllers in a REST. It can be used to define and organize the endpoints of a web service. The
 * annotation includes an optional {@code value} attribute, which allows users to associate a custom value or identifier
 * with the annotated controller.</p>
 *
 * <p>Example of usage:</p>
 *
 * <pre>
 * {@code @RestController("customValue")}
 *
 * public class CustomController {
 *     // Controller class definition
 * }
 * </pre>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface RestController {
    String value() default "";
}