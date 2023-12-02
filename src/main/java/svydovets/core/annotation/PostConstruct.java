package svydovets.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *     The PostConstruct annotation is used on method that needs to be executed after the bean
 *     is fully initialized and all dependency injection has been performed.
 * </p>
 * <p>
 *     When bean creates, it first calls the constructor, then it injects dependencies,
 *     and finally it calls the method annotated with @PostConstruct.
 *     This method can contain any logic that you need to execute after bean is fully created and
 *     all its dependencies are set.
 * </p>
 *
 * <p>
 *     Here is how it works:
 * </p>
 *
 * <pre class="code">
 * &#064;Component
 * public class ServiceWithPostConstruct {
 *     private String message;
 *     &#064;PostConstruct
 *     public void init() {
 *         message = "Message loaded with @PostConstruct";
 *     }
 *     public String getMessage() {
 *         return message;
 *     }
 * }
 * </pre>
 * <p>
 *     In this class, init method is annotated with @PostConstruct.
 *     This means that after ServiceWithPostConstruct instance creation,
 *     it will call the init method. Init method then sets value of message field.
 * </p>
 * <p>
 *     After ServiceWithPostConstruct instance is fully initialized,
 *     init method is automatically called, setting value of message field
 *     to "Message loaded with @PostConstruct". This means that whenever retrieve
 *     message field using the getMessage method, it will return "Message loaded with @PostConstruct".
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PostConstruct {
}
